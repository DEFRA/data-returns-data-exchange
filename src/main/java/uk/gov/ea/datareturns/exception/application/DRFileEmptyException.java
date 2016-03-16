/**
 * 
 */
package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * Thrown by the service if the uploaded file is empty
 * 
 * @author Sam Gardner-Dell
 */
public class DRFileEmptyException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public DRFileEmptyException(String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.FILE_EMPTY.getAppStatusCode(), message);
	}
}