/**
 *
 */
package uk.gov.ea.datareturns.exception.application;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * @author Sam Gardner-Dell
 *
 */
public class UnrecognisedFieldException extends AbstractValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public UnrecognisedFieldException(final String message) {
		super(message);
	}

	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND;
	}
}
