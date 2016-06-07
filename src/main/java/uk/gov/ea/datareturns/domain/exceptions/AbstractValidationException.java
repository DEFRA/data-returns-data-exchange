/**
 *
 */
package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Exception type to signify when a validation error occurred when validating the model generated from the input
 * file.
 *
 * @author Sam Gardner-Dell
 */
public abstract class AbstractValidationException extends ProcessingException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct an implementation of the {@link AbstractValidationException}
	 *
	 * @param message the detailed exception message
	 */
	public AbstractValidationException(final String message) {
		super(message);
	}

	/**
	 * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
	 *
	 * @return the appropriate ApplicationExceptionType for this exception
	 */
	public abstract ApplicationExceptionType getType();
}
