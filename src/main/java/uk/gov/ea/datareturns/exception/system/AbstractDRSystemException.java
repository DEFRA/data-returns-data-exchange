package uk.gov.ea.datareturns.exception.system;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.exception.AbstractDRException;

public abstract class AbstractDRSystemException extends AbstractDRException
{
	private static final long serialVersionUID = 1L;

	public AbstractDRSystemException(Throwable cause, Status htmlStatusCode, int appStatusCode, String message)
	{
		super(cause,  htmlStatusCode, appStatusCode,  message);
	}
}