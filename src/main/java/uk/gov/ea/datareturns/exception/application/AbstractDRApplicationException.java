package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.AbstractDRException;
import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;
import uk.gov.ea.datareturns.exception.system.AbstractDRSystemException;

public abstract class AbstractDRApplicationException extends AbstractDRException
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDRSystemException.class);
	private static final long serialVersionUID = 1L;

	public AbstractDRApplicationException(Status code, int appStatusCode, String message)
	{
		super(Response.status(code).entity(new ExceptionMessageContainer(appStatusCode, message)).type(APPLICATION_JSON).build());
	}

	public AbstractDRApplicationException(int code, String message)
	{
		super("An error has occured (" + code + ") - " + message);

		LOGGER.error("An error has occured (" + code + ") - " + message);
	}
}