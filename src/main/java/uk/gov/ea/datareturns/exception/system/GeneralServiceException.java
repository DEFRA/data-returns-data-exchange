package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static uk.gov.ea.datareturns.type.SystemException.SERVICE;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class GeneralServiceException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public GeneralServiceException(Throwable cause, String message)
	{
		super(cause, Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ExceptionMessageContainer(SERVICE.getCode(), message))
				.type(MediaType.APPLICATION_JSON)
				.build());
	}
}