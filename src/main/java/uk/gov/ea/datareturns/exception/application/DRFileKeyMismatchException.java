package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.FILE_KEY_MISMATCH;

public class DRFileKeyMismatchException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRFileKeyMismatchException(String message)
	{
		super(OK, FILE_KEY_MISMATCH.getAppStatusCode(), message);
	}
}