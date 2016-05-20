/**
 *
 */
package uk.gov.ea.datareturns.domain.exceptions;

/**
 * @author Sam Gardner-Dell
 *
 */
public abstract class AbstractValidationException extends ProcessingException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public AbstractValidationException(final String message) {
		super(message);
	}

	/**
	 * @return the appropriate ApplicationExceptionType for this exception
	 */
	public abstract ApplicationExceptionType getType();
}
