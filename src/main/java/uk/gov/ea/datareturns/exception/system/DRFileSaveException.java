package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemException.FILE_SAVE;

public class DRFileSaveException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRFileSaveException(Throwable cause, String message)
	{
		super(cause, OK, FILE_SAVE.getCode(), message);
	}
}