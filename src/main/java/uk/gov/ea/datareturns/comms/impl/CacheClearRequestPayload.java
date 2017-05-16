package uk.gov.ea.datareturns.comms.impl;

import uk.gov.ea.datareturns.comms.Payload;
import uk.gov.ea.datareturns.distributedtransaction.RemoteCache;

import java.io.Serializable;

import static uk.gov.ea.datareturns.comms.impl.CacheClearRequestPayload.Type.CONFIRMED;
import static uk.gov.ea.datareturns.comms.impl.CacheClearRequestPayload.Type.REQUESTED;

/**
 * This payload is for attached to a message is a request for a blocking remote
 * cache clear
 */
public class CacheClearRequestPayload implements Payload, Serializable {
    private final RemoteCache.Cache cache;
    private final Type type;

    public enum Type { CONFIRMED, REQUESTED }

    private CacheClearRequestPayload(RemoteCache.Cache cache) {
        this.cache = cache;
        this.type = REQUESTED;
    }

    public CacheClearRequestPayload(RemoteCache.Cache cache, Type type) {
        this.cache = cache;
        this.type = type;
    }

    public static CacheClearRequestPayload instanceOf(RemoteCache.Cache cache) {
        return new CacheClearRequestPayload(cache);
    }

    public static CacheClearRequestPayload cleared(CacheClearRequestPayload payload) {
        return new CacheClearRequestPayload(payload.getCache(), CONFIRMED);
    }

    public RemoteCache.Cache getCache() {
        return cache;
    }

    public Type getType() {
        return type;
    }
}
