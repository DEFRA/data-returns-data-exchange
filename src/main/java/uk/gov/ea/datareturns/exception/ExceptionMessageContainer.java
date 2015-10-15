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

	public ExceptionMessageContainer(int appStatusCode)
	{
		this.appStatusCode = appStatusCode;
	}

	public ExceptionMessageContainer(String message)
	{
		this.message = message;
	}

	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public void setAppStatusCode(int appStatusCode)
	{
		this.appStatusCode = appStatusCode;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}