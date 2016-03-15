package uk.gov.ea.datareturns.exception.system;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.AbstractDRException;
import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public abstract class AbstractDRSystemException extends AbstractDRException
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDRSystemException.class);
	private static final long serialVersionUID = 1L;

	public AbstractDRSystemException(Throwable cause, Status code, int appStatusCode, String message)
	{
		super(cause, Response.status(code).entity(new ExceptionMessageContainer(appStatusCode, message)).type(MediaType.APPLICATION_JSON).build());
		LOGGER.error("A system error has occurred: " + message, cause);
	}
	

	public AbstractDRSystemException(Status code, int appStatusCode, String message)
	{
		super(Response.status(code).entity(new ExceptionMessageContainer(appStatusCode, message)).type(MediaType.APPLICATION_JSON).build());
		LOGGER.error("A system error has occurred: " + message);
	}
}