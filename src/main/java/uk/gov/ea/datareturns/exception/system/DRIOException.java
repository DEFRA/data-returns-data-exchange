/**
 * 
 */
package uk.gov.ea.datareturns.exception.system;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * @author sam
 *
 */
public class DRIOException extends AbstractDRSystemException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param cause
	 * @param message
	 */
	public DRIOException(Throwable cause, String message) {
		super(cause, Status.INTERNAL_SERVER_ERROR, ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode(), message);
	}

}
