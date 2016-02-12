package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.TRANSFORMER;

public class DRTransformerException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRTransformerException(Throwable cause, String message)
	{
		super(cause, OK, TRANSFORMER.getCode(), message);
	}
}