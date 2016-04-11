package uk.gov.ea.datareturns.exception.application;

import javax.ws.rs.core.Response.Status;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRHeaderMandatoryFieldMissingException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRHeaderMandatoryFieldMissingException(final String message) {
		super(Status.BAD_REQUEST, ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode(), message);
	}
}
