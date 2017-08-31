package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

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
    private final UniqueIdentifierSetDao uniqueIdentifierSetDao;

    public class SitePermitServiceException extends Exception {
        public SitePermitServiceException(String message) {
            super(message);
        }
    }

    @Inject
    public SitePermitService(
            UniqueIdentifierSetDao uniqueIdentifierSetDao,
            SiteDao siteDao,
            UniqueIdentifierDao uniqueIdentifierDao,
            UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
            Search search) {

        this.uniqueIdentifierSetDao = uniqueIdentifierSetDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;
    }

    @Transactional(readOnly = true)
    public UniqueIdentifier getUniqueIdentifierByName(String eaIdId) {
        return uniqueIdentifierDao.getByName(eaIdId);
    }

    @Transactional(readOnly = true)
    public List<UniqueIdentifier> listUniqueIdentifiers(
            UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType) {
        return uniqueIdentifierDao.list(uniqueIdentifierSetType);
    }

    @Transactional(readOnly = true)
    public List<UniqueIdentifier> listUniqueIdentifiers(
            UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType, Operator operator) {
        return uniqueIdentifierDao.list(uniqueIdentifierSetType, operator);
    }

    private UniqueIdentifierSet getUniqueSetFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType) throws SitePermitServiceException {
        List<UniqueIdentifierSet> sets = uniqueIdentifierSetDao.listSetsFor(uniqueIdentifierSetType);

        if (sets.size() != 1) {
            throw new SitePermitServiceException(uniqueIdentifierSetType + "Does not define a unique UniqueIdentifier set");
        }

        return sets.get(0);
    }

    private UniqueIdentifierSet getUniqueSetFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType,
                                                Operator operator) throws SitePermitServiceException {
        List<UniqueIdentifierSet> sets = uniqueIdentifierSetDao.listSetsFor(uniqueIdentifierSetType, operator);

        if (sets.size() != 1) {
            throw new SitePermitServiceException("Operator: " + operator.getName() + " with "
                    + uniqueIdentifierSetType + "Does not define a unique UniqueIdentifierSet");
        }

        return sets.get(0);
    }

    private void addNewPermitAndSite(String eaId, UniqueIdentifierSet uniqueIdentifierSet,  String siteName,
                                    String[] aliasNames) {

        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);
        uniqueIdentifier.setUniqueIdentifierSet(uniqueIdentifierSet);

        UniqueIdentifierAlias[] uniqueIdentifierAliases = new UniqueIdentifierAlias[aliasNames.length];

        for (int i = 0; i < aliasNames.length; i++) {
            uniqueIdentifierAliases[i] = new UniqueIdentifierAlias();
            uniqueIdentifierAliases[i].setUniqueIdentifier(uniqueIdentifier);
            uniqueIdentifierAliases[i].setName(aliasNames[i]);
        }

        siteDao.add(site);
        uniqueIdentifierDao.add(uniqueIdentifier);

        if (aliasNames != null && aliasNames.length > 0) {
            for (int i = 0; i < aliasNames.length; i++) {
                uniqueIdentifierAliasDao.add(uniqueIdentifierAliases[i]);
            }
        }

        // Reset the local and remote caches
        resetLocalCaches();
    }

    @Transactional
    public void addNewPermitAndSite(String eaId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType,
                                    Operator operator,
                                    String siteName) throws SitePermitServiceException {

        addNewPermitAndSite(eaId, getUniqueSetFor(uniqueIdentifierSetType, operator), siteName, null);
    }

    @Transactional
    public void addNewPermitAndSite(String eaId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType,
                                    Operator operator,
                                    String siteName,
                                    String[] aliasNames) throws SitePermitServiceException {

        addNewPermitAndSite(eaId, getUniqueSetFor(uniqueIdentifierSetType, operator),
                siteName, aliasNames);
    }

    @Transactional
    public void addNewPermitAndSite(String eaId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType,
                                    String siteName) throws SitePermitServiceException {

        addNewPermitAndSite(eaId, getUniqueSetFor(uniqueIdentifierSetType), siteName, null);
    }

    @Transactional
    public void addNewPermitAndSite(String eaId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType,
                                    String siteName,
                                    String[] aliasNames) throws SitePermitServiceException {

        addNewPermitAndSite(eaId, getUniqueSetFor(uniqueIdentifierSetType),
                siteName, aliasNames);
    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    @Transactional
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

            // Reset the local and remote caches
            resetLocalCaches();

        } else {
            LOGGER.warn("Requested removal of non-existent permit: " + eaId);
        }
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
