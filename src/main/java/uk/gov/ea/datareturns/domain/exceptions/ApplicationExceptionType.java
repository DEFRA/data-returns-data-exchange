package uk.gov.ea.datareturns.domain.exceptions;

/**
 * Enumeration of the different exceptions that can occur processing a file.
 *
 * @author Sam Gardner-Dell
 */
public enum ApplicationExceptionType {
	/** DR0400 - File type unsupported */
	FILE_TYPE_UNSUPPORTED(400, "Unsupported file type"),
	/** DR0450 - File structure error */
	FILE_STRUCTURE_EXCEPTION(450, "File structure error"),
	/** DR0500 - Empty file error */
	FILE_EMPTY(500, "Empty file"),
	/** DR0820 - Missing mandatory heading */
	HEADER_MANDATORY_FIELD_MISSING(820, "Mandatory headings missing"),
	/** DR0840 - Unrecognised field found */
	HEADER_UNRECOGNISED_FIELD_FOUND(840, "Unrecognised field found"),
	/** DR0900 - validation errors in file content */
	VALIDATION_ERRORS(900, "One or more validation problems are present and must be corrected"),
	/** DR3000 - unexpected system error */
	SYSTEM_FAILURE(3000, "A system failure occurred");

	private final int appStatusCode;

	private final String reason;

	/**
	 * Enum constructor
	 *
	 * @param appStatusCode the application error status code
	 * @param reason textual description for the error type
	 */
	ApplicationExceptionType(final int appStatusCode, final String reason) {
		this.appStatusCode = appStatusCode;
		this.reason = reason;
	}

	/**
	 * @return the application status code
	 */
	public int getAppStatusCode() {
		return this.appStatusCode;
	}

	/**
	 * @return the description for the error type
	 */
	public String getReason() {
		return this.reason;
	}
}
