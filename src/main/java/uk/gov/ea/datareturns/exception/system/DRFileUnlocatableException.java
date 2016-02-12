package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_UNLOCATABLE;

public class DRFileUnlocatableException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRFileUnlocatableException(Throwable cause, String message)
	{
		super(cause, OK, FILE_UNLOCATABLE.getCode(), message);
	}
}