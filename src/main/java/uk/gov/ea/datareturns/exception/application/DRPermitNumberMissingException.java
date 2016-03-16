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
public class DRPermitNumberMissingException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRPermitNumberMissingException(String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.PERMIT_NUMBER_MISSING.getAppStatusCode(), message);
	}
}
