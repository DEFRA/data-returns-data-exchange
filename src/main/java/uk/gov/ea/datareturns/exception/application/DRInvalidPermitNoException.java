package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.INVALID_PERMIT_NO;

public class DRInvalidPermitNoException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRInvalidPermitNoException(String message)
	{
		super(OK, INVALID_PERMIT_NO.getAppStatusCode(), message);
	}
}