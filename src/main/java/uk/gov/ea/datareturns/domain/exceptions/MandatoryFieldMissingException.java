package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Exception type to signify when a mandatory field was missing from the input file
 *
 * @author Sam Gardner-Dell
 */
public class MandatoryFieldMissingException extends AbstractValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new {@link MandatoryFieldMissingException}
	 *
	 * @param message the detailed exception message
	 */
	public MandatoryFieldMissingException(final String message) {
		super(message);
	}

	/**
	 * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
	 *
	 * @return the appropriate ApplicationExceptionType for this exception
	 */
	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING;
	}
}