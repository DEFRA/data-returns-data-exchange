package uk.gov.ea.datareturns.exception.application;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public class FileTypeUnsupportedException extends AbstractValidationException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public FileTypeUnsupportedException(final String message) {
		super(message);
	}

	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.FILE_TYPE_UNSUPPORTED;
	}
}