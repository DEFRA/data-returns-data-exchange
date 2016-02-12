package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemExceptionType.CONVERSION;

public class DRConversionException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRConversionException(Throwable cause, String message)
	{
		super(cause, OK, CONVERSION.getCode(), message);
	}
}