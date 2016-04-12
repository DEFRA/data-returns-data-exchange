package uk.gov.ea.datareturns.type;

public enum ApplicationExceptionType {
	// DR0400
	FILE_TYPE_UNSUPPORTED(400, "Unsupported File Type"),
	// DR0450
	INCONSISTENT_CSV_RECORD(450, "Inconsistent CSV record"),
	// DR0500
	FILE_EMPTY(500, "Empty file"),
	// DR0820
	HEADER_MANDATORY_FIELD_MISSING(820, "Mandatory headings missing."),
	// DR0840
	HEADER_UNRECOGNISED_FIELD_FOUND(840, "Unrecognised field found."),
	// DR0900
	VALIDATION_ERRORS(900, "One or more validation problems are present and must be corrected."),
	// DR3000
	SYSTEM_FAILURE(3000, "A system failure occurred");

	private int appStatusCode;
	private String reason;

	ApplicationExceptionType(final int appStatusCode, final String reason) {
		this.appStatusCode = appStatusCode;
		this.reason = reason;
	}

	public int getAppStatusCode() {
		return this.appStatusCode;
	}

	public String getReason() {
		return this.reason;
	}
}
