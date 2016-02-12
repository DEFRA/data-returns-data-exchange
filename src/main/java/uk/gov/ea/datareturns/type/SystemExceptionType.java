package uk.gov.ea.datareturns.type;

public enum SystemExceptionType
{
	FILE_SAVE(601, "Cannot Save File"), 
	FILE_UNLOCATABLE(602, "Cannot Locate File"), 
	FILE_READ(603, "Cannot Read File"),
	NOTIFICATION(604, "Notification failed"),
	SERVICE(605, "Service failure"),
	FILE_DELETE(606, "Cannot Delete File"), 
	VALIDATION(607, "Validation failed"), 
	TRANSFORMER(608, "Transformation failed"), 
	SERIALIZATION(609, "Serialize failed"), 
	DESERIALIZE(610, "Deserialize failed"), 
	CONVERSION(611, "Conversion failed"), 
	
	UNPROCESSABLE_ENTITY(422, "Unprocessable entity"); // Must be available in some standard lib somewhere? (not just Spring)

	private final int code;
	private final String reason;

	SystemExceptionType(int code, String reason)
	{
		this.code = code;
		this.reason = reason;
	}

	public int getCode()
	{
		return code;
	}

	public String getReason()
	{
		return reason;
	}
}
