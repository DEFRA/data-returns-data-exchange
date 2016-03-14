/**
 * 
 */
package uk.gov.ea.datareturns.domain.io.csv.exceptions;

/**
 * @author Sam Gardner-Dell
 */
public class HeaderFieldUnrecognisedException extends ValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public HeaderFieldUnrecognisedException(String message) {
		super(message);
	}
	
	public HeaderFieldUnrecognisedException(String message, Throwable cause) {
		super(message, cause);
	}
}
