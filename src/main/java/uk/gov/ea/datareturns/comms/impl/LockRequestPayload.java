package uk.gov.ea.datareturns.comms.impl;

import uk.gov.ea.datareturns.comms.Payload;
import uk.gov.ea.datareturns.distributedtransaction.LockSubject;

import java.io.Serializable;

/**
 * The payload used to request and release remote locks
 */
public class LockRequestPayload implements Payload, Serializable {

    private final RequestType requestType;
    private Result result;
    private LockSubject lockSubject;
    private LockRequestPayload lockRequestPayload;

    public enum RequestType {
        ACQUIRE, RELEASE
    }

    public enum Result {
        ACQUIRED, RELEASED, NOT_ACQUIRED
    }

    public static LockRequestPayload acquireRequest(LockSubject lockSubject) {
        return new LockRequestPayload(lockSubject, RequestType.ACQUIRE);
    }

    public static LockRequestPayload releaseRequest(LockSubject lockSubject) {
        return new LockRequestPayload(lockSubject, RequestType.RELEASE);
    }

    public static LockRequestPayload acquired(LockRequestPayload lockRequestPayload) {
        return new LockRequestPayload(lockRequestPayload.lockSubject, RequestType.ACQUIRE, Result.ACQUIRED);
    }

    public static LockRequestPayload notAcquired(LockRequestPayload lockRequestPayload) {
        return new LockRequestPayload(lockRequestPayload.lockSubject, RequestType.ACQUIRE, Result.NOT_ACQUIRED);
    }

    public static LockRequestPayload released(LockRequestPayload lockRequestPayload) {
        return new LockRequestPayload(lockRequestPayload.lockSubject, RequestType.RELEASE, Result.RELEASED);
    }

    private LockRequestPayload(LockSubject lockSubject, RequestType requestType) {
        this.lockSubject = lockSubject;
        this.requestType = requestType;
    }

    private LockRequestPayload(LockSubject lockSubject, RequestType requestType, Result result) {
        this.lockSubject = lockSubject;
        this.requestType = requestType;
        this.result = result;
    }

    public LockSubject getLockSubject() {
        return lockSubject;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Result getResult() {
        return result;
    }
}
