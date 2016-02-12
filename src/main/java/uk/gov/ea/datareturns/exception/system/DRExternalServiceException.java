package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.SERVICE;

public class DRExternalServiceException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRExternalServiceException(Throwable cause, String message)
	{
		super(cause, OK, SERVICE.getCode(), message);
	}
}