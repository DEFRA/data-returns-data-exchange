/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv.generic.exceptions;

/**
 * @author sam
 *
 */
public class HeaderFieldMissingException extends ValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public HeaderFieldMissingException(final String message) {
		super(message);
	}
}
