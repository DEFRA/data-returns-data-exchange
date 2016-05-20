/**
 *
 */
package uk.gov.ea.datareturns.domain.storage;

import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * General storage exception class
 *
 * @author Sam Gardner-Dell
 */
public class StorageException extends ProcessingException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public StorageException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StorageException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
