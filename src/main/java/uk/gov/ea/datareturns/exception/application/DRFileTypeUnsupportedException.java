package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRFileTypeUnsupportedException extends AbstractDRApplicationException {
	private static final long serialVersionUID = 1L;

	public DRFileTypeUnsupportedException(final String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getAppStatusCode(), message);
	}
}