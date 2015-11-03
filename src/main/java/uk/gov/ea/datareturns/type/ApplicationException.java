package uk.gov.ea.datareturns.type;

public enum ApplicationException
{
	INVALID_FILE_TYPE(701, "Invalid File Type"), 
	EMPTY_FILE(702, "Empty File"), 
	INSUFFICIENT_DATA(703, "Insufficient Data"), 
	FILE_KEY_MISMATCH(704, "File Key Mismatch"),
	INVALID_FILE_CONTENTS(705, "File Key Mismatch"),
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
