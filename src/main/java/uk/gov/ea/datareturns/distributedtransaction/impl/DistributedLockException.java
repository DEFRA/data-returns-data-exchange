package uk.gov.ea.datareturns.distributedtransaction.impl;

import org.springframework.dao.DataAccessException;

/**
 * Created by graham on 30/03/17.
 */
public class DistributedLockException extends DataAccessException {
    public DistributedLockException(String msg) {
        super(msg);
    }
}
