package uk.gov.ea.datareturns.type;

public enum AppStatusCodeType
{
	APP_STATUS_SUCCESS(800, "Success"), APP_STATUS_FAILED_WITH_ERRORS(801, "Failed with errors");

	private int appStatusCode;
	private String reason;

	AppStatusCodeType(int appStatusCode, String reason)
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
