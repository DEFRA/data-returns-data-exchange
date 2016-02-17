package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.SERIALIZATION;

public class DRDeserializationException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRDeserializationException(Throwable cause, String message)
	{
		super(cause, OK, SERIALIZATION.getCode(), message);
	}
}