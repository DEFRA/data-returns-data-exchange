package uk.gov.ea.datareturns.exception;

public final class ExceptionMessageContainer
{
	private int appStatusCode;
	private String message;

	public ExceptionMessageContainer(int appStatusCode, String message)
	{
		this.appStatusCode = appStatusCode;
		this.message = message;
	}

	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public String getMessage()
	{
		return message;
	}
}