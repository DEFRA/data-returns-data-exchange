package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemException.VALIDATION;

public class DRFileValidationException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRFileValidationException(Throwable cause, String message)
	{
		super(cause, OK, VALIDATION.getCode(), message);
	}
}