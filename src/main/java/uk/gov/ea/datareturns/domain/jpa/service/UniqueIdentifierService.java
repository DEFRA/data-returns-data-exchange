package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The functions to support the EA_ID lookup and its aliases
 * @author Graham Willis
 */
@Service
public class UniqueIdentifierService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(UniqueIdentifierService.class);

    private UniqueIdentifierDao uniqueIdentifierDao;
    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private SiteDao siteDao;

    private volatile Map<String, Set<String>> cacheAliasByBaseName = null;
    private volatile Map<String, Set<UniqueIdentifier>> cacheBaseNameBySite = null;

    @Inject
    public UniqueIdentifierService(UniqueIdentifierDao uniqueIdentifierDao, UniqueIdentifierAliasDao uniqueIdentifierAliasDao, SiteDao siteDao) {
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.siteDao = siteDao;
    }

    /**
     * Build the cache lookup for the set of permit numbers by the base permit number
     * @return
     */
    private Map<String, Set<String>> getAliasCache() {
        if (cacheAliasByBaseName == null) {
            synchronized (this) {
                if (cacheAliasByBaseName == null) {
                    LOGGER.info("Build cache: Alias by base name");
                    List<UniqueIdentifierAlias> list = uniqueIdentifierAliasDao.list();
                    cacheAliasByBaseName = list.stream().collect(
                            Collectors.groupingBy(e -> e.getUniqueIdentifier().getName(),
                                    Collectors.mapping(e -> e.getName(), Collectors.toSet())
                            )
                    );
                }
            }
        }
        return cacheAliasByBaseName;
    }

    private Map<String, Set<UniqueIdentifier>> getCacheBaseNameBySite() {
        if (cacheBaseNameBySite == null) {
            synchronized (this) {
                if (cacheBaseNameBySite == null) {
                    LOGGER.info("Build cache: Base name by site");
                    List<UniqueIdentifier> list = uniqueIdentifierDao.list();
                    cacheBaseNameBySite = list.stream().collect(Collectors.groupingBy(
                            e -> e.getSite().getName(), Collectors.toSet()));
                }
            }
        }
        return cacheBaseNameBySite;
    }

    /**
     * Get list of alias names for a given UniqueIdentifier
     * @param uniqueIdentifier
     * @return Alias names
     */
    public Set<String> getAliasNames(UniqueIdentifier uniqueIdentifier) {
        return getAliasCache().get(uniqueIdentifier.getName());
    }

    /**
     * Get a unique identifier from (exact) site name
     */
    public Set<UniqueIdentifier> getUniqueIdentifierBySiteName(String siteName) {
        return getCacheBaseNameBySite().get(siteName);
    }

    /**
     * Determine the UniqueIdentifier from its name or aliases name
     * @param name or aliases name
     * @return
     */
    public UniqueIdentifier getUniqueIdentifier(String name) {
        UniqueIdentifier ui = uniqueIdentifierDao.getByName(name);
        if (ui != null) {
            return ui;
        } else {
            UniqueIdentifierAlias uia = uniqueIdentifierAliasDao.getByName(name);
            if (uia != null) {
                return uia.getUniqueIdentifier();
            } else {
                return null;
            }
        }
    }

    /**
     * Test if a unique identifier from its name or alias name
     * @param name
     * @return
     */
    public boolean uniqueIdentifierExists(String name) {
        return getUniqueIdentifier(name) != null ? true : false;
    }

    /**
     * Get the site from the unique identifier
     * @param uniqueIdentifier
     * @return The site
     */
    public Site getSite(UniqueIdentifier uniqueIdentifier) {
        return uniqueIdentifier.getSite();
    }

    /**
     * Get the site from the unique identifier alias
     * @param uniqueIdentifierAlias
     * @return the site
     */
    public Site getSite(UniqueIdentifierAlias uniqueIdentifierAlias) {
        return uniqueIdentifierAlias.getUniqueIdentifier().getSite();
    }

    /**
     * Get the list of all permit numbers from the uniqueIdentifierName
     * @param name unique identifier name
     * @return List of all permit numbers
     */
    public Set<String> getAllUniqueIdentifierNames(String name) {
        UniqueIdentifier ui = getUniqueIdentifier(name);
        Set<String> aliasNames = getAliasNames(ui);
        aliasNames.add(ui.getName());
        return aliasNames;
    }
}
