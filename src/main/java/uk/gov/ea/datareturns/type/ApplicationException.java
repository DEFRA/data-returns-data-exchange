package uk.gov.ea.datareturns.type;

public enum ApplicationException
{
	UNSUPPORTED_FILE_TYPE(700, "Unsupported File Type"), 
	INVALID_CONTENTS(701, "Invalid contents"), 
	NO_RETURNS(702, "No Returns"), 
	MULTIPLE_RETURNS(703, "Multiple Returns"), 
	MULTIPLE_PERMITS(704, "Multiple Permits"), 
//	UNKNOWN_RETURN_TYPE(702, "Unknown Return Type"), 
//	INSUFFICIENT_DATA(702, "Insufficient Data"), 
//	MISSING_RETURN_TYPE(703, "Missing Return Type"), 
	
	// TODO needed?
	INVALID_FILE_TYPE(704, "Invalid File Type"), 
	FILE_KEY_MISMATCH(705, "File Key Mismatch"),
	DATABASE_CONFIG(706, "Database configuration"),
	PERMIT_NOT_FOUND(707, "Permit not found");

	private int appStatusCode;
	private String reason;

	ApplicationException(int appStatusCode, String reason)
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
