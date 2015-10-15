package uk.gov.ea.datareturns.type;

public enum SystemException
{
	FILE_SAVE(601, "Cannot Save File"), 
	FILE_UNLOCATABLE(602, "Cannot Locate File"), 
	FILE_READ(603, "Cannot Read File"),
	NOTIFICATION(604, "Notification failed");

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
