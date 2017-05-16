package uk.gov.ea.datareturns.distributedtransaction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Graham Willis
 *
 * LockSubject provides a reentrant lock to
 * guard the transactions on a service and gives the lock a description
 *
 * The re-enternat lock maybe either acquired locally or remotely but is always
 * the singleton
 */
public final class LockSubject implements Serializable {

    // The subject of the locking
    public enum Subject {
        SITE_PERMIT
    }

    private Subject subject;
    private final ReentrantLock lock = new ReentrantLock();
    private static Map<Subject, LockSubject> lockSubjectMap = new HashMap<>();

    private LockSubject(Subject subject) {
        this.subject = subject;
    }

    public static LockSubject instanceOf(Subject subject) {
        if (!lockSubjectMap.containsKey(subject)) {
            lockSubjectMap.put(subject, new LockSubject(subject));
        }
        return lockSubjectMap.get(subject);
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Subject getSubject() {
        return subject;
    }
}
