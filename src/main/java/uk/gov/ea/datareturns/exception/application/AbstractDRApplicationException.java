package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.exception.AbstractDRException;
import uk.gov.ea.datareturns.exception.ExceptionMessageContainer;

public abstract class AbstractDRApplicationException extends AbstractDRException {
	private static final long serialVersionUID = 1L;

	public AbstractDRApplicationException(final Status code, final int appStatusCode, final String message) {
		super(Response.status(code).entity(new ExceptionMessageContainer(appStatusCode, message)).type(APPLICATION_JSON)
				.build());
	}
}