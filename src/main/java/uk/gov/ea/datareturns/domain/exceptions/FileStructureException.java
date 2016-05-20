/**
 *
 */
package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Thrown if a structural error is encountered when trying to read a file.
 *
 * E.g. a row read from a CSV file has an inconsistent number of fields with respect to the header definitions
 *
 * @author Sam Gardner-Dell
 */
public class FileStructureException extends AbstractValidationException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public FileStructureException(final String message) {
		super(message);
	}

	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION;
	}
}
