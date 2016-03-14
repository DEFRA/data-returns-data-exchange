/**
 * 
 */
package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * @author sam
 *
 */
public class DRUnrecognisedFieldException extends AbstractDRApplicationException {
	private static final long serialVersionUID = 1L;

	public DRUnrecognisedFieldException(String message)
	{
		super(OK, ApplicationExceptionType.UNRECOGNISED_FIELD_FOUND.getAppStatusCode(), message);
	}
}
