package uk.gov.ea.datareturns.distributedtransaction;

/**
 * Created by graham on 05/04/17.
 */
public interface RemoteCache {
    boolean blockingRemoteCacheClear(Cache cache);
    void clearCacheLocal(Cache cache);

    enum Cache {
        SITE_PERMIT_CACHES
    }
}
