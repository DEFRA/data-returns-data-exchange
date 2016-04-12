/**
 * 
 */
package uk.gov.ea.datareturns.domain.io.csv.generic.exceptions;

/**
 * Thrown by the reader if a row is encountered with an inconsistent number of fields with respect to the header.
 * 
 * @author Sam Gardner-Dell
 */
public class InconsistentRowException extends ValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InconsistentRowException(String message) {
		super(message);
	}
}
