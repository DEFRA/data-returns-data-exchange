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

	/**
	 * Create a new {@link FileStructureException}
	 *
	 * @param message the detailed exception message
	 */
	public FileStructureException(final String message) {
		super(message);
	}

	/**
	 * Retrieve the {@link ApplicationExceptionType} which relates to this Exception
	 *
	 * @return the appropriate ApplicationExceptionType for this exception
	 */
	@Override
	public ApplicationExceptionType getType() {
		return ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION;
	}
}
