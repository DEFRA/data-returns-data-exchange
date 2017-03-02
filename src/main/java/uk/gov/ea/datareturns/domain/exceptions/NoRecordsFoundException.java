package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Thrown by the service if the uploaded file does not contain any records
 *
 * @author Sam Gardner-Dell
 */
public class NoRecordsFoundException extends AbstractValidationException {
    /** Appease the gods of serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link NoRecordsFoundException}
     *
     * @param message the detailed exception message
     */
    public NoRecordsFoundException(final String message) {
        super(message);
    }

    /**
     * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
     *
     * @return the appropriate ApplicationExceptionType for this exception
     */
    @Override
    public ApplicationExceptionType getType() {
        return ApplicationExceptionType.NO_RECORDS;
    }
}