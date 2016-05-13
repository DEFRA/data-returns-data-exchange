/**
 *
 */
package uk.gov.ea.datareturns.exception.application;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * Thrown by the service if the uploaded file is empty
 *
 * @author Sam Gardner-Dell
 */
public class FileEmptyException extends AbstractValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public FileEmptyException(final String message) {
		super(message);
	}

	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.FILE_EMPTY;
	}
}