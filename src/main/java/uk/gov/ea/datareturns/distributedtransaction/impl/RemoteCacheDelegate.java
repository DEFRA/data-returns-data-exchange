package uk.gov.ea.datareturns.distributedtransaction.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.comms.InterAPICoordinator;
import uk.gov.ea.datareturns.distributedtransaction.RemoteCache;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.service.Search;

import javax.inject.Inject;

/**
 * @author Graham Willis
 * Spring component to provide remote cache operations
 */
@Component
public class RemoteCacheDelegate implements RemoteCache {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RemoteCacheDelegate.class);
    private final SiteDao siteDao;
    private final UniqueIdentifierDao uniqueIdentifierDao;
    private final UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private final Search search;

    @Inject
    public RemoteCacheDelegate(SiteDao siteDao,
            UniqueIdentifierDao uniqueIdentifierDao,
            UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
            Search search) {

        this.siteDao = siteDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;
    }

    @Override
    public boolean blockingRemoteCacheClear(Cache cache) {
        return InterAPICoordinator.runningInstanceOf().clearRemoteCache(cache);
    }

    public void clearCacheLocal(Cache cache) {
        switch (cache) {
            case SITE_PERMIT_CACHES:
                siteDao.clearCaches();
                uniqueIdentifierDao.clearCaches();
                uniqueIdentifierAliasDao.clearCaches();
                search.clearIndexes();
                LOGGER.info("Cleared (local) cache group: " + cache);
                break;

            default:
                LOGGER.error("Not implemented: " + cache);
        }
    }
}
