package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import uk.gov.ea.datareturns.distributedtransaction.DistributedTransactionService;
import uk.gov.ea.datareturns.distributedtransaction.LockSubject;
import uk.gov.ea.datareturns.distributedtransaction.RemoteCache;
import uk.gov.ea.datareturns.distributedtransaction.impl.DistributedLockException;
import uk.gov.ea.datareturns.distributedtransaction.impl.DistributedTransactionServiceImpl;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;

import javax.inject.Inject;
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
    private final PlatformTransactionManager transactionManager;
    private final DistributedTransactionServiceImpl.DistributedTransactionLock distributedTransactionLock;
    private final RemoteCache remoteCache;

    @Inject
    public SitePermitService(SiteDao siteDao,
                             UniqueIdentifierDao uniqueIdentifierDao,
                             UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
                             Search search,
                             PlatformTransactionManager transactionManager,
                             RemoteCache remoteCacheService,
                             DistributedTransactionService distributedTransactionService) {

        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;
        this.transactionManager = transactionManager;
        this.remoteCache = remoteCacheService;
        this.distributedTransactionLock = distributedTransactionService.distributedTransactionLockFor(LockSubject.instanceOf(LockSubject.Subject.SITE_PERMIT));
    }

    /**
     * Add a new permit site and associated aliases
     * Any exception occurring in this function will cause the transaction to be rolled back
     *
     * @param eaId
     * @param siteName
     * @param aliasNames
     */
    public void addNewPermitAndSite(String eaId, String siteName, String[] aliasNames)  {

        // Acquire a distributed lock on all remote servers for this transaction
        distributedTransactionLock.globalLockAction(() -> {

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("addNewPermitAndSite");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

            TransactionStatus status = transactionManager.getTransaction(def);

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

            // Reset the local and remote caches
            if (remoteCache.blockingRemoteCacheClear(RemoteCache.Cache.SITE_PERMIT_CACHES)) {
                remoteCache.clearCacheLocal(RemoteCache.Cache.SITE_PERMIT_CACHES);
            } else {
                throw new DistributedLockException("Could not clear remote cache");
            }

            // This can cause an exception on write to the database so that transaction is rolled back the
            // proceeding cache clear functions are not called.
            transactionManager.commit(status);
        });
    }

    /**
     * Add a new permit and site without aliases. Clears the cache
     * @param eaId
     * @param siteName
     */
    public void addNewPermitAndSite(String eaId, String siteName) {

        // Acquire a distributed lock on all remote servers for this transaction
        distributedTransactionLock.globalLockAction(() -> {

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("addNewPermitAndSite");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

            TransactionStatus status = transactionManager.getTransaction(def);

            Site site = new Site();
            site.setName(siteName);

            UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
            uniqueIdentifier.setName(eaId);
            uniqueIdentifier.setSite(site);

            siteDao.add(site);
            uniqueIdentifierDao.add(uniqueIdentifier);

            // Reset the local and remote caches
            if (remoteCache.blockingRemoteCacheClear(RemoteCache.Cache.SITE_PERMIT_CACHES)) {
                remoteCache.clearCacheLocal(RemoteCache.Cache.SITE_PERMIT_CACHES);
            } else {
                throw new DistributedLockException("Could not clear remote cache");
            }

            // Commit the database transaction
            transactionManager.commit(status);

        });
    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    public void removePermitSiteAndAliases(String eaId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(Key.explicit(eaId));

        if (uniqueIdentifier != null) {

            // Acquire a distributed lock on all remote servers for this transaction
            distributedTransactionLock.globalLockAction(() -> {

                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("removePermitSiteAndAliases");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

                TransactionStatus status = transactionManager.getTransaction(def);

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
                if (remoteCache.blockingRemoteCacheClear(RemoteCache.Cache.SITE_PERMIT_CACHES)) {
                    remoteCache.clearCacheLocal(RemoteCache.Cache.SITE_PERMIT_CACHES);
                } else {
                    throw new DistributedLockException("Could not clear remote cache");
                }

                // Commit the database transaction
                transactionManager.commit(status);

            });
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

    public void resetLocalCacheWithLocking() {
        distributedTransactionLock.globalLockAction(() -> resetLocalCaches());
    }



}
