package uk.gov.ea.datareturns.exception;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public abstract class AbstractDRException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public AbstractDRException(Status htmlStatusCode, int appStatusCode, String message)
	{
		super(Response.status(htmlStatusCode)
				.entity(new ExceptionMessageContainer(appStatusCode, message))
				.type(APPLICATION_JSON)
				.build());
	}

	public AbstractDRException(Throwable cause, Status htmlStatusCode, int appStatusCode, String message)
	{
		super(cause, Response.status(htmlStatusCode)
				.entity(new ExceptionMessageContainer(appStatusCode, message))
				.type(MediaType.APPLICATION_JSON)
				.build());
	}
}