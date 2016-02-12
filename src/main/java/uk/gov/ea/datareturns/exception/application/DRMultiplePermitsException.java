package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.MULTIPLE_PERMITS;

public class DRMultiplePermitsException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRMultiplePermitsException(String message)
	{
		super(OK, MULTIPLE_PERMITS.getAppStatusCode(), message);
	}
}