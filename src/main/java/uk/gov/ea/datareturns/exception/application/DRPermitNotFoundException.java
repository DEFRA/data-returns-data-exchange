package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationException.PERMIT_NOT_FOUND;

public class DRPermitNotFoundException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRPermitNotFoundException(String message)
	{
		super(OK, PERMIT_NOT_FOUND.getAppStatusCode(), message);
	}
}