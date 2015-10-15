package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static uk.gov.ea.datareturns.type.SystemException.NOTIFICATION;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public class NotificationException extends WebApplicationException
{
	private static final long serialVersionUID = 1L;

	public NotificationException(String message)
	{
		super(Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ExceptionMessageContainer(NOTIFICATION.getCode(), message))
				.type(APPLICATION_JSON)
				.build());
	}
}