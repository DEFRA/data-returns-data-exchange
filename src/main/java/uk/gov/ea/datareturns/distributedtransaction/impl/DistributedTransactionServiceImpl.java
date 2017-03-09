package uk.gov.ea.datareturns.distributedtransaction.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.comms.InterAPICoordinator;
import uk.gov.ea.datareturns.distributedtransaction.DistributedTransactionService;
import uk.gov.ea.datareturns.distributedtransaction.LockSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Graham
 * Spring managed bean clear the remote cache
 */
@Component
public class DistributedTransactionServiceImpl implements DistributedTransactionService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DistributedTransactionServiceImpl.class);

    private final static long timeout = 5000;
    private final static TimeUnit unit = TimeUnit.MILLISECONDS;
    private InterAPICoordinator.RemoteLockRequest remoteLockAcquireRequest;
    private InterAPICoordinator.RemoteLockRequest remoteLockReleaseRequest;

    /**
     * No no instantiation using new
     */
    private DistributedTransactionServiceImpl() {

    }

    public void remoteLockAcquireRequest(InterAPICoordinator.RemoteLockAcquireRequest request) {
        this.remoteLockAcquireRequest = request;
    }

    public void remoteLockReleaseRequest(InterAPICoordinator.RemoteLockReleaseRequest request) {
        this.remoteLockReleaseRequest = request;
    }

    /**
     * This interface used to provide a run action for the transactions to
     * be guarded by the lock
     */
    @FunctionalInterface
    public interface LockedActions {
        void run() throws InterruptedException;
    }

    /**
     * The lock object to be used to lock a given service designated by the lock subject.
     * The lock subject contains the actual reentrant lock for the service being guarded
     */
    public class DistributedTransactionLock {
        private boolean lockAcquired = false;

        LockSubject locksubject;

        DistributedTransactionLock(LockSubject locksubject) {
            this.locksubject = locksubject;
        }

        /**
         * Perform transaction locked operations
         * @param lockedActions
         */
        public void action(LockedActions lockedActions) {
            // Acquire a remote
            if (remoteLockAcquireRequest.get(locksubject)) {
                // Try to acquire a local lock on the transaction
                try {

                    LOGGER.info("Requesting locked transaction: " + locksubject.getSubject());
                    lockAcquired = locksubject.getLock().tryLock(timeout, unit);

                    if (lockAcquired) {
                        LOGGER.info("Starting locked transaction: " + locksubject.getSubject());

                        // Run the enclosed transaction action
                        lockedActions.run();
                    } else {
                        // We could not acquire the lock within the given timeout so we
                        // throw the lock exception to terminate the transaction
                        throw new DistributedLockException("Timeout occurred acquiring transaction lock: " + locksubject.getSubject());
                    }
                } catch (InterruptedException e) {
                    // This should not happen as this thread is not interrupted by the application
                    throw new DistributedLockException("Transaction thread interrupted within: " + locksubject.getSubject());
                } finally {
                    if (lockAcquired) {
                        LOGGER.info("Releasing locked transaction: " + locksubject.getSubject());
                        locksubject.getLock().unlock();
                    }
                    // Release the remote locks
                    remoteLockReleaseRequest.get(locksubject);
                }
            } else {
                // Failed to acquire the remote lock
                throw new DistributedLockException("Could not acquire: " + locksubject.getSubject());
            }
        }

        /**
         * Function to acquire the lock - this is called by the remote server
         * and the lock is held by the current thread. The lock is waited on - it must be acquired
         */
        public boolean acquire() {
            // Try to acquire a lock on the transaction
            try {
                LOGGER.info("Requesting lock: " + locksubject.getSubject());
                lockAcquired = locksubject.getLock().tryLock(timeout, unit);

                if (lockAcquired) {
                    LOGGER.info("Acquired lock: " + locksubject.getSubject());
                    return true;
                } else {
                    // We could not acquire the lock within the given timeout
                    LOGGER.info("Failed to acquire lock: " + locksubject.getSubject());
                    return false;
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
                return false;
            }
        }

        /**
         * Function to release the lock if held by the current thread
         */
        public void release() {
            if (locksubject.getLock().isHeldByCurrentThread()) {
                LOGGER.info("Releasing lock: " + locksubject.getSubject());
                locksubject.getLock().unlock();
            }
        }
    }

    private static Map<LockSubject, DistributedTransactionLock> distributedTransactionLockMap = new HashMap<>();

    public synchronized DistributedTransactionLock distributedTransactionLockFor(LockSubject locksubject) {
        if (!distributedTransactionLockMap.containsKey(locksubject)) {
            distributedTransactionLockMap.put(locksubject, new DistributedTransactionLock(locksubject));
        }
        return distributedTransactionLockMap.get(locksubject);
    }
}
