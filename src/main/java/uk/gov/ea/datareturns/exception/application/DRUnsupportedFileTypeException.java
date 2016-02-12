package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.ApplicationException.UNSUPPORTED_FILE_TYPE;

public class DRUnsupportedFileTypeException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DRUnsupportedFileTypeException(String message)
	{
		super(OK, UNSUPPORTED_FILE_TYPE.getAppStatusCode(), message);
	}
}