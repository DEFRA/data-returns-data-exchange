/**
 * 
 */
package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * @author sam
 *
 */
public class DRHeaderFieldUnrecognisedException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRHeaderFieldUnrecognisedException(String message) {
		super(Status.OK, ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND.getAppStatusCode(), message);
	}
}
