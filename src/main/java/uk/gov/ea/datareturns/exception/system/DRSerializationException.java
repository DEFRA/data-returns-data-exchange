package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemException.DESERIALIZE;

public class DRSerializationException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRSerializationException(Throwable cause, String message)
	{
		super(cause, OK, DESERIALIZE.getCode(), message);
	}
}