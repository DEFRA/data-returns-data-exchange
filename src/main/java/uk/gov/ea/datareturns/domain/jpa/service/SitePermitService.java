package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

/**
 * @author Graham Willis
 * Transactional service layer to perform common operations on the site and permit data
 *
 * This particular service does not use declarative transactions becuase of the need to
 * re-build the lucene index only on a sucessful commit and only when all the atomic updates have completed.
 *
 * An exception may be thrown by the commit which throws out to the caller
 */
@Service
public class SitePermitService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SitePermitService.class);

    private final UniqueIdentifierDao uniqueIdentifierDao;
    private final SiteDao siteDao;
    private final UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private final Search search;



    public class SitePermitServiceException extends Exception {
        public SitePermitServiceException(String message) {
            super(message);
        }
    }

    @Inject
    public SitePermitService(
            SiteDao siteDao,
            UniqueIdentifierDao uniqueIdentifierDao,
            UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
            Search search) {

        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;
    }

    // Gets the extended graph from the cache
    public UniqueIdentifier getUniqueIdentifierByName(String eaIdId) {
        return uniqueIdentifierDao.getByName(eaIdId);
    }

    @Transactional
    public void addNewPermitAndSite(String eaId, String siteName,
                                    String[] aliasNames) {

        Instant timestamp = Instant.now();

        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);
        uniqueIdentifier.setDatasetChangedDate(timestamp);
        uniqueIdentifier.setCreateDate(timestamp);
        uniqueIdentifier.setLastChangedDate(timestamp);

        siteDao.add(site);
        uniqueIdentifierDao.add(uniqueIdentifier);

        if (aliasNames != null && aliasNames.length > 0) {
            for (int i = 0; i < aliasNames.length; i++) {
                UniqueIdentifierAlias uniqueIdentifierAlias = new UniqueIdentifierAlias();
                uniqueIdentifierAlias.setUniqueIdentifier(uniqueIdentifier);
                uniqueIdentifierAlias.setName(aliasNames[i]);
                uniqueIdentifierAliasDao.add(uniqueIdentifierAlias);
            }
        }

        resetLocalCaches();
    }

    @Transactional
    public void addNewPermitAndSite(String eaId, String siteName) throws SitePermitServiceException {
        addNewPermitAndSite(eaId, siteName, null);
    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    @Transactional
    public void removePermitSiteAndAliases(String eaId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(Key.explicit(eaId));

        // The aliases are removed by association
        if (uniqueIdentifier != null) {
            Site site = uniqueIdentifier.getSite();

            uniqueIdentifierDao.removeById(uniqueIdentifier.getId());
            siteDao.removeById(site.getId());

            // Reset the local and remote caches
            resetLocalCaches();

        } else {
            LOGGER.warn("Requested removal of non-existent permit: " + eaId);
        }
    }

    @Transactional(readOnly = true)
    public List<UniqueIdentifier> listUniqueIdentifiers() {
        return uniqueIdentifierDao.list();
    }

    /**
     * Unlocked call to set local caches called from within the local transaction
     */
    private void resetLocalCaches() {
        // Reinitialize the lucene cache
        search.initialize();

        // Clear the local caches
        uniqueIdentifierDao.clearCaches();
        uniqueIdentifierAliasDao.clearCaches();
        siteDao.clearCaches();
    }
}
