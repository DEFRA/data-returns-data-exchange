package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Exception type to signify when an unsupported file type was submitted to the service.
 *
 * @author Sam Gardner-Dell
 */
public class FileTypeUnsupportedException extends AbstractValidationException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link FileTypeUnsupportedException}
     *
     * @param message the detailed exception message
     */
    public FileTypeUnsupportedException(final String message) {
        super(message);
    }

    /**
     * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
     *
     * @return the appropriate ApplicationExceptionType for this exception
     */
    @Override
    public ApplicationExceptionType getType() {
        return ApplicationExceptionType.FILE_TYPE_UNSUPPORTED;
    }
}