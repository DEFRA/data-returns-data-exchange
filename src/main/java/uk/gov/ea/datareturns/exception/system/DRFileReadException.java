package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_READ;

public class DRFileReadException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRFileReadException(Throwable cause, String message)
	{
		super(cause, OK, FILE_READ.getCode(), message);
	}
}