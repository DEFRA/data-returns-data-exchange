package uk.gov.defra.datareturns.exceptions;

/**
 * Exception type to signify when a validation error occurred when validating the model generated from the input
 * file.
 *
 * @author Sam Gardner-Dell
 */
public class ValidationException extends ProcessingException {
    /**
     * Appease the gods of serialization
     */
    private static final long serialVersionUID = 1L;

    private final ApplicationExceptionType type;

    /**
     * Construct an implementation of the {@link ValidationException}
     *
     * @param message the detailed exception message
     */
    public ValidationException(final ApplicationExceptionType type, final String message) {
        super(message);
        this.type = type;
    }

    public ApplicationExceptionType getType() {
        return type;
    }
}
