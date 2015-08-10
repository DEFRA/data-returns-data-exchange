package uk.gov.ea.datareturns.resources;


public class UploadError
{
	String reason;
	String lineNo;
	String columnName;
	String errValue;
	
	public UploadError()
	{
		this.reason = "test reason";
		this.lineNo = "12345";
		this.columnName = "colname";
		this.errValue = "test err value";
	}

	public UploadError(String errLine)
	{
		parseErrorMessage(errLine);
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public String getLineNo()
	{
		return lineNo;
	}

	public void setLineNo(String lineNo)
	{
		this.lineNo = lineNo;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getErrValue()
	{
		return errValue;
	}

	public void setErrValue(String errValue)
	{
		this.errValue = errValue;
	}

	private void parseErrorMessage(String message)
	{
		int beginIndex = -1;
		int endIndex = -1;
		String reason;
		String lineNo;
		String columnName;
		String errValue;

		reason = message.substring(0, message.indexOf(" "));
		
		beginIndex = message.indexOf("line:") + 6;
		endIndex = message.indexOf(",", beginIndex);
		lineNo = message.substring(beginIndex, endIndex);

		beginIndex = message.indexOf("column:") + 8;
		endIndex = message.indexOf(",", beginIndex);
		columnName = message.substring(beginIndex, endIndex);

		beginIndex = message.indexOf("value:") + 7;
		errValue = message.substring(beginIndex);

		this.reason = reason;
		this.lineNo = lineNo;
		this.columnName = columnName;
		this.errValue = errValue;
	}


}