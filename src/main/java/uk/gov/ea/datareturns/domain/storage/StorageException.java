package uk.gov.ea.datareturns.domain.storage;

import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * General storage exception class
 *
 * @author Sam Gardner-Dell
 */
public class StorageException extends ProcessingException {
    /** Appease the gods of serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link StorageException}
     *
     * @param message the detailed exception message
     */
    public StorageException(final String message) {
        super(message);
    }

    /**
     * Create a new {@link StorageException}
     *
     * @param message the detailed exception message
     * @param cause the underlying exception which resulted in this exception being thrown
     */
    public StorageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}