package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRMandatoryFieldsMissingException extends AbstractDRApplicationException {

	private static final long serialVersionUID = 1L;

	public DRMandatoryFieldsMissingException(String message)
	{
		super(OK, ApplicationExceptionType.MANDATORY_FIELDS_MISSING.getAppStatusCode(), message);
	}
}
