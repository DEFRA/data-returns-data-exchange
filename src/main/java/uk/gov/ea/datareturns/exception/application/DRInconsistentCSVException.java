/**
 *
 */
package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * Thrown if a row read from a CSV file has an inconsistent number of fields with respect to the header definitions
 *
 * @author Sam Gardner-Dell
 */
public class DRInconsistentCSVException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRInconsistentCSVException(final String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.INCONSISTENT_CSV_RECORD.getAppStatusCode(), message);
	}
}
