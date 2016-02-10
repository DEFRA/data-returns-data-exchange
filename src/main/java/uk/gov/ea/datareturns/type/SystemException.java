package uk.gov.ea.datareturns.type;

// TODO UNPROCESSABLE_ENTITY must be available in some standard lib somewhere? (not just Spring)
// TODO renumber +
public enum SystemException
{
	FILE_SAVE(601, "Cannot Save File"), 
	FILE_UNLOCATABLE(602, "Cannot Locate File"), 
	FILE_READ(603, "Cannot Read File"),
	NOTIFICATION(604, "Notification failed"),
	SERVICE(605, "Service failure"),
	FILE_DELETE(606, "Cannot Delete File"), 
	UNPROCESSABLE_ENTITY(422, "Unprocessable entity");

	private final int code;
	private final String reason;

	SystemException(int code, String reason)
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
