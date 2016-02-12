package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.COLUMN_NAME_NOT_FOUND;

public class DRColumnNameNotFoundException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRColumnNameNotFoundException(String message)
	{
		super(OK, COLUMN_NAME_NOT_FOUND.getAppStatusCode(), message);
	}
}