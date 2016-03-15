package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRPermitNotRecognisedException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRPermitNotRecognisedException(String message) {
		super(Status.OK, ApplicationExceptionType.PERMIT_NOT_RECOGNISED.getAppStatusCode(), message);
	}
}