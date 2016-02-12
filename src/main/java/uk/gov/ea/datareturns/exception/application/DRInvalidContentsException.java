package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_CONTENTS;

public class DRInvalidContentsException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRInvalidContentsException(String message)
	{
		super(OK, INVALID_CONTENTS.getAppStatusCode(), message);
	}
}