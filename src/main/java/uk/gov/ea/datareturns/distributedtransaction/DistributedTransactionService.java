package uk.gov.ea.datareturns.distributedtransaction;

import uk.gov.ea.datareturns.comms.InterAPICoordinator;
import uk.gov.ea.datareturns.distributedtransaction.impl.DistributedTransactionServiceImpl;

/**
 * @author Graham Willis
 * Interface to define remote cache operations used by service layer transactions
 */
public interface DistributedTransactionService {
    <T extends LockSubject> DistributedTransactionServiceImpl.DistributedTransactionLock distributedTransactionLockFor(T lockClass);

    void remoteLockAcquireRequest(InterAPICoordinator.RemoteLockAcquireRequest request);
    void remoteLockReleaseRequest(InterAPICoordinator.RemoteLockReleaseRequest request);
}
