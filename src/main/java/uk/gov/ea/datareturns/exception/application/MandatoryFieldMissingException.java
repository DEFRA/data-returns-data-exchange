package uk.gov.ea.datareturns.exception.application;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class MandatoryFieldMissingException extends AbstractValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public MandatoryFieldMissingException(final String message) {
		super(message);
	}

	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING;
	}
}