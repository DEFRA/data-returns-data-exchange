package uk.gov.defra.datareturns.exceptions;

/**
 * Generic processing exception and the root type for all application exceptions.
 *
 * @author Sam Gardner-Dell
 */
public class ProcessingException extends RuntimeException {
    /**
     * Appease the gods of serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link ProcessingException} for the given message
     *
     * @param message the detailed exception message
     */
    public ProcessingException(final String message) {
        super(message);
    }

    /**
     * Create a new {@link ProcessingException} for the given cause
     *
     * @param cause the underlying cause for the {@link ProcessingException}
     */
    public ProcessingException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message the detailed exception message
     * @param cause   the underlying cause for the {@link ProcessingException}
     */
    public ProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
