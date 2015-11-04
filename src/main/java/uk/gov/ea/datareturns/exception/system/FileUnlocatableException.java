package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static uk.gov.ea.datareturns.type.SystemException.FILE_UNLOCATABLE;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class FileUnlocatableException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public FileUnlocatableException(String message)
	{
		super(Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ExceptionMessageContainer(FILE_UNLOCATABLE.getCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}

	public FileUnlocatableException(Throwable cause, String message)
	{
		super(cause, Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ExceptionMessageContainer(FILE_UNLOCATABLE.getCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}
}