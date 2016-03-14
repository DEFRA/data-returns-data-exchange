package uk.gov.ea.datareturns.type;

public enum ApplicationExceptionType
{
	UNSUPPORTED_FILE_TYPE(700, "Unsupported File Type"), 
	INVALID_CONTENTS(701, "Invalid contents"), 
	NO_RETURNS(702, "No Returns"), 
	MULTIPLE_PERMITS(703, "Multiple Permits"), 
	PERMIT_NOT_FOUND(704, "Permit not found"),
	FILE_KEY_MISMATCH(705, "File Key Mismatch"),
	COLUMN_NAME_NOT_FOUND(706, "Column name not found in schema error message"), 
	INVALID_PERMIT_NO(707, "Invalid Permit number"), 
	ENVIRONMENT(708, "Environment configuration failure"),
	
	MANDATORY_FIELDS_MISSING(709, "Mandatory headings missing."),
	UNRECOGNISED_FIELD_FOUND(710, "Unrecognised field found.");
	

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
