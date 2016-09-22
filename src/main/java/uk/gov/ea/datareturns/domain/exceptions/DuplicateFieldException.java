package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Exception type to signify when an duplicate field was found in the input file
 *
 * @author Sam Gardner-Dell
 */
public class DuplicateFieldException extends AbstractValidationException {
    /** Appease the gods of serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link DuplicateFieldException}
     *
     * @param message the detailed exception message
     */
    public DuplicateFieldException(final String message) {
        super(message);
    }

    /**
     * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
     *
     * @return the appropriate ApplicationExceptionType for this exception
     */
    @Override
    public ApplicationExceptionType getType() {
        return ApplicationExceptionType.HEADER_DUPLICATE_FIELD_FOUND;
    }
}
