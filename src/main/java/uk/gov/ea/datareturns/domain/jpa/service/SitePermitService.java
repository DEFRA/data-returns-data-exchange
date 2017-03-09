package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author Graham Willis
 * Transactional service layer to perform common operations on the site and permit data
 */
@Service
@Transactional
public class SitePermitService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SitePermitService.class);

    private final UniqueIdentifierDao uniqueIdentifierDao;
    private final SiteDao siteDao;
    private final UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private final Search search;

    @Inject
    public SitePermitService(SiteDao siteDao,
                                  UniqueIdentifierDao uniqueIdentifierDao,
                                  UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
                                  Search search) {

        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;
    }

    /**
     * Add a new permit site and associated aliases
     * @param eaId
     * @param siteName
     * @param aliasNames
     */
    public void addNewPermitAndSite(String eaId, String siteName, String[] aliasNames)  {
        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);

        UniqueIdentifierAlias[] uniqueIdentifierAliases = new UniqueIdentifierAlias[aliasNames.length];

        for (int i = 0; i < aliasNames.length; i++) {
            uniqueIdentifierAliases[i] = new UniqueIdentifierAlias();
            uniqueIdentifierAliases[i].setUniqueIdentifier(uniqueIdentifier);
            uniqueIdentifierAliases[i].setName(aliasNames[i]);
        }

        siteDao.add(site);
        uniqueIdentifierDao.add(uniqueIdentifier);

        for (int i = 0; i < aliasNames.length; i++) {
            uniqueIdentifierAliasDao.add(uniqueIdentifierAliases[i]);
        }

        search.initialize();
    }

    /**
     * Add a new permit and site without aliases. Clears the cache
     * @param eaId
     * @param siteName
     */
    public void addNewPermitAndSite(String eaId, String siteName) {
        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);

        siteDao.add(site);
        uniqueIdentifierDao.add(uniqueIdentifier);

        search.initialize();
    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    public void removePermitSiteAndAliases(String eaId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(Key.explicit(eaId));
        if (uniqueIdentifier != null) {
            Site site = uniqueIdentifier.getSite();
            Set<String> aliasNames = uniqueIdentifierDao.getAliasNames(uniqueIdentifier);
            if (aliasNames != null) {
                for (String aliasName : aliasNames) {
                    UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName(Key.explicit(aliasName));
                    uniqueIdentifierAliasDao.removeById(uniqueIdentifierAlias.getId());
                }
            }
            uniqueIdentifierDao.removeById(uniqueIdentifier.getId());
            siteDao.removeById(site.getId());

            search.initialize();
        } else {
            LOGGER.warn("Requested removal of non-existent permit: " + eaId);
        }
    }
}
