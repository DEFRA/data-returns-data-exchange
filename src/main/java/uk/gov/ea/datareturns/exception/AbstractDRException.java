package uk.gov.ea.datareturns.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class AbstractDRException extends WebApplicationException {
	private static final long serialVersionUID = 1L;

	public AbstractDRException(final Throwable cause, final Response response) {
		super(cause, response);
	}

	public AbstractDRException(final Response response) {
		super(response);
	}
}