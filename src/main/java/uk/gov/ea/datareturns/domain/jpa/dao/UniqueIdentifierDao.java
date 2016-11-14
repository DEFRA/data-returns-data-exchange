package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO for return periods
 *
 * @author Graham Willis
 *
 * The Unique identifier (EA_ID) and its aliases do NOT use the
 * standard aliasing mechanism. (This is because the data strucutre
 * differs for the entities uniqueIdentifier, uniqueIdentifierAlias and Site
 *
 * The UniqueIdentifiersService is the service level aggregator for teh functionality
 * connecting these entities

 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierDao extends EntityDao<UniqueIdentifier> {

    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;

    private volatile Map<String, Set<String>> cacheAliasByBaseName = null;
    private volatile Map<String, Set<UniqueIdentifier>> cacheBaseNameBySite = null;

    @Inject
    public UniqueIdentifierDao(UniqueIdentifierAliasDao uniqueIdentifierAliasDao) {
        super(UniqueIdentifier.class);
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    public String getKeyFromRelaxedName(String name) {
        return name;
    }

    public UniqueIdentifier getByName(String name) {
        UniqueIdentifier ui = getCache().get(name);
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
     * Determine the UniqueIdentifier from its name or aliases name
     * @param name or aliases name. No relaxation allowed - never build that cache
     * @return
     */
    public UniqueIdentifier getByNameRelaxed(String name) {
        return getByName(name);
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
     * Test if a unique identifier from its name or alias name
     * @param name
     * @return
     */
    public boolean uniqueIdentifierExists(String name) {
        return getByNameRelaxed(name) != null ? true : false;
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
        UniqueIdentifier ui = getByNameRelaxed(name);
        Set<String> aliasNames = getAliasNames(ui);
        aliasNames.add(ui.getName());
        return aliasNames;
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
                    List<UniqueIdentifier> list = list();
                    cacheBaseNameBySite = list.stream().collect(Collectors.groupingBy(
                            e -> e.getSite().getName(), Collectors.toSet()));
                }
            }
        }
        return cacheBaseNameBySite;
    }


}