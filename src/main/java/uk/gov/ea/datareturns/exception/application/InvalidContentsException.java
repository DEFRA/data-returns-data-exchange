package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_CONTENTS;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class InvalidContentsException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public InvalidContentsException(String message)
	{
		super(Response.status(OK)
				.entity(new ExceptionMessageContainer(INVALID_CONTENTS.getAppStatusCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}
}