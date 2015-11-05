package uk.gov.ea.datareturns.domain;

import java.util.HashMap;
import java.util.Map;

public class DataExchangeError
{
	String reason;
	String lineNo;
	String columnName;
	String errValue;

	private Map<String, String> meaningfulReasons = new HashMap<String, String>();
	private Map<String, String> helpfulExamples = new HashMap<String, String>();

	public DataExchangeError()
	{
		this.reason = "";
		this.lineNo = "";
		this.columnName = "";
		this.errValue = "";

		this.meaningfulReasons.put("xDate", "Invalid date");
		this.meaningfulReasons.put("range", "Invalid range");
		this.meaningfulReasons.put("is", "Invalid value");
		this.meaningfulReasons.put("regex", "Invalid character(s)");
//		this.meaningfulReasons.put("notReg", "Permit Not Registered");

		this.helpfulExamples.put("xDate", "This date must be included in the format 'YYYY-MM-DD' eg 2015-10-31");
		this.helpfulExamples.put("range", "Value should be between 1970 and 2999");
		this.helpfulExamples.put("is", "This value must be either 'Metres' or 'Mbar'");
		this.helpfulExamples.put("regex", "This value must only contain letters or numbers");
//		this.helpfulExamples.put("notReg", "You can only submit returns for Permits registered to you");
	}

	public DataExchangeError(String errLine)
	{
		this();
		parseErrorMessage(errLine);
	}

	public DataExchangeError(String reason, String lineNo, String columnName, String errValue)
	{
		this();

		this.reason = reason;
		this.lineNo = lineNo;
		this.columnName = columnName;
		this.errValue = errValue;
	}

	public String getReason()
	{
		return this.reason;
	}

	public String getMeaningfulReason()
	{
		String errCode = getErrorCode();
		
		if(errCode == null)
		{
			return "Error text not found"; 
		} else
		{
			return this.meaningfulReasons.get(errCode);
		}
	}

	public String getHelpfulExample()
	{
		String errCode = getErrorCode();
		
		if(errCode == null)
		{
			return "Example text not found"; 
		} else
		{
			return this.helpfulExamples.get(errCode);
		}
	}

	public String getErrorCode()
	{
		int chopPos = this.reason.indexOf("(");

		if (chopPos == -1)
		{
			return this.reason;
		} else
		{
			return this.reason.substring(0, chopPos);
		}
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

	// Probably some nice regex to do this
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
		this.lineNo = Integer.toString(Integer.parseInt(lineNo) + 1); // Allow for header row, maybe option available in lib?
		this.columnName = columnName;
		this.errValue = errValue;
	}

}