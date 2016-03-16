package uk.gov.ea.datareturns.exception.system;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRExternalServiceException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRExternalServiceException(Throwable cause, String message)
	{
		super(cause, Status.INTERNAL_SERVER_ERROR, ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode(), message);
	}
}