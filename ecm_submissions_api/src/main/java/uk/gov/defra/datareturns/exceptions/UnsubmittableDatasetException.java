package uk.gov.defra.datareturns.exceptions;

/**
 * Exception thrown when attempting to submit a dataset that contains invalid records.
 *
 * @author Sam Gardner-Dell
 */
public class UnsubmittableDatasetException extends ProcessingException {
    /**
     * Appease the gods of serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link UnsubmittableDatasetException} for the given message
     *
     * @param message the detailed exception message
     */
    public UnsubmittableDatasetException(final String message) {
        super(message);
    }

    /**
     * Create a new {@link UnsubmittableDatasetException} for the given cause
     *
     * @param cause the underlying cause for the {@link UnsubmittableDatasetException}
     */
    public UnsubmittableDatasetException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message the detailed exception message
     * @param cause   the underlying cause for the {@link UnsubmittableDatasetException}
     */
    public UnsubmittableDatasetException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
