package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRPermitNotUniqueException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRPermitNotUniqueException(String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.PERMIT_NOT_UNIQUE.getAppStatusCode(), message);
	}
}