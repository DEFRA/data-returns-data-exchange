package uk.gov.ea.datareturns.exception.system;

import static javax.ws.rs.core.Response.Status.OK;
import static uk.gov.ea.datareturns.type.SystemException.NOTIFICATION;

public class DRNotificationException extends AbstractDRSystemException
{
	private static final long serialVersionUID = 1L;

	public DRNotificationException(Throwable cause, String message)
	{
		super(cause, OK, NOTIFICATION.getCode(), message);
	}
}