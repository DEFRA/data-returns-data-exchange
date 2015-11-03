package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static uk.gov.ea.datareturns.type.ApplicationException.PERMIT_NOT_FOUND;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class PermitNotFoundException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public PermitNotFoundException(String message)
	{
		super(Response.status(BAD_REQUEST)
				.entity(new ExceptionMessageContainer(PERMIT_NOT_FOUND.getAppStatusCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}
}