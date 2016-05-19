/**
 *
 */
package uk.gov.ea.datareturns.exception.application;

/**
 * Generic processing exception and the root type for all application exceptions.
 *
 * @author Sam Gardner-Dell
 */
public class ProcessingException extends Exception {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ProcessingException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProcessingException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}