package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRFileKeyMismatchException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public DRFileKeyMismatchException(final String message) {
		super(Status.NOT_FOUND, ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode(), message);
	}
}
