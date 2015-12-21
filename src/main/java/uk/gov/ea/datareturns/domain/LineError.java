package uk.gov.ea.datareturns.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author adrianharrison
 * Holds Schema validation error details for a single XML Element.
 * Some properties are held at this level (not in ErrorDetail) in case multiple errors are later required
 */
public class LineError
{
	@JacksonXmlProperty(localName = "ColumnName")
	@JsonInclude(Include.NON_NULL)
	private String columnName;

	@JacksonXmlProperty(localName = "ErrorValue")
	private String errorValue;
	
	@JacksonXmlProperty(localName = "InputLineNo")
	@JsonInclude(Include.NON_NULL)
	private String inputLineNo;

	@JacksonXmlProperty(localName = "OutputLineNo")
	@JsonInclude(Include.NON_NULL)
	private String outputLineNo;

	@JacksonXmlProperty(localName = "ErrorDetail")
	@JsonInclude(Include.NON_NULL)
	private ErrorDetail errorDetail;

	public LineError()
	{
	}

	public LineError(String columnName, String lineNo, String errorLevel, String errorMessage)
	{
		this.columnName = columnName;
		this.inputLineNo = lineNo;
		this.errorDetail = new ErrorDetail(errorLevel, errorMessage);
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getErrorValue()
	{
		return errorValue;
	}

	public void setErrorValue(String errorValue)
	{
		this.errorValue = errorValue;
	}

	public String getInputLineNo()
	{
		return inputLineNo;
	}

	public void setInputLineNo(String inputLineNo)
	{
		this.inputLineNo = inputLineNo;
	}

	public String getOutputLineNo()
	{
		return outputLineNo;
	}

	public void setOutputLineNo(String outputLineNo)
	{
		this.outputLineNo = outputLineNo;
	}

	public ErrorDetail getErrorDetail()
	{
		return errorDetail;
	}

	public void setErrorDetail(ErrorDetail errorDetail)
	{
		this.errorDetail = errorDetail;
	}
}