package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.exception.AbstractDRException;

public abstract class AbstractDRApplicationException extends AbstractDRException
{
	private static final long serialVersionUID = 1L;

	public AbstractDRApplicationException(Status htmlStatusCode, int appStatusCode, String message)
	{
		super(htmlStatusCode, appStatusCode, message);
	}
}