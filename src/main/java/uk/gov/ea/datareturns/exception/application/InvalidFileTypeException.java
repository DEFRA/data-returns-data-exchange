package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_FILE_TYPE;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class InvalidFileTypeException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public InvalidFileTypeException(String message)
	{
		super(Response.status(BAD_REQUEST)
				.entity(new ExceptionMessageContainer(INVALID_FILE_TYPE.getAppStatusCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}
}