package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationException.NO_RETURNS;

public class DRNoReturnsException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRNoReturnsException(String message)
	{
		super(OK, NO_RETURNS.getAppStatusCode(), message);
	}
}