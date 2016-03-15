package uk.gov.ea.datareturns.exception.application;

import static javax.ws.rs.core.Response.Status.OK;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class DRHeaderMandatoryFieldMissingException extends AbstractDRApplicationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public DRHeaderMandatoryFieldMissingException(String message) {
		super(OK, ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode(), message);
	}
}
