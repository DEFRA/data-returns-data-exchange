/**
 * 
 */
package uk.gov.ea.datareturns.storage;

/**
 * General storage exception class
 * 
 * @author Sam Gardner-Dell
 */
public class StorageException extends Exception {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
