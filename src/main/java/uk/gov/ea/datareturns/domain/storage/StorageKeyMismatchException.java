/**
 *
 */
package uk.gov.ea.datareturns.domain.storage;

/**
 * Thrown if the {@link StorageProvider} is unable to retrieve a file for a particular key
 *
 * @author Sam Gardner-Dell
 */
public class StorageKeyMismatchException extends StorageException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new {@link StorageKeyMismatchException}
	 *
	 * @param message the detailed exception message
	 */
	public StorageKeyMismatchException(final String message) {
		super(message);
	}
}
