package uk.gov.ea.datareturns.type;

public enum ApplicationExceptionType
{
	// DR0400
	FILE_TYPE_UNSUPPORTED(400, "Unsupported File Type"),
	// DR0500
	FILE_EMPTY(500, "Empty file"),
	// DR0700
	PERMIT_NOT_UNIQUE(700, "Multiple permits found"),
	// DR0800
	PERMIT_NOT_RECOGNISED(800, "Permit not recognised"),
	// DR0810
	PERMIT_NUMBER_MISSING(810, "Permit number missing"),
	// DR0820
	HEADER_MANDATORY_FIELD_MISSING(820, "Mandatory headings missing."),
	// DR0840
	HEADER_UNRECOGNISED_FIELD_FOUND(840, "Unrecognised field found."),
	// DR3000
	SYSTEM_FAILURE(3000, "A system failure occurred");
	
//	FILE_KEY_MISMATCH(705, "File Key Mismatch"),
	
	
	
	
//	ENVIRONMENT(708, "Environment configuration failure");	

	private int appStatusCode;
	private String reason;

	ApplicationExceptionType(int appStatusCode, String reason)
	{
		this.appStatusCode = appStatusCode;
		this.reason = reason;
	}

	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public String getReason()
	{
		return reason;
	}
}
