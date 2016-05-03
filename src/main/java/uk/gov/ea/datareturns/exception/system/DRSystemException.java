package uk.gov.ea.datareturns.exception.system;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRSystemException extends AbstractDRSystemException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRSystemException(final Throwable cause, final String message) {
		super(cause, Status.INTERNAL_SERVER_ERROR, ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode(), message);
	}

	public DRSystemException(final String message) {
		super(Status.INTERNAL_SERVER_ERROR, ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode(), message);
	}

	public DRSystemException(final Throwable cause) {
		this(cause, cause.getMessage());
	}
}
