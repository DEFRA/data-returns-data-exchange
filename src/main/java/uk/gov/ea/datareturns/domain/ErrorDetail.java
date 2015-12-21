package uk.gov.ea.datareturns.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ErrorDetail
{
	@JacksonXmlProperty(localName = "ErrorLevel")
	private String errorLevel;

	@JacksonXmlCData
	@JacksonXmlProperty(localName = "InputMessage")
	private String inputMessage;

	@JacksonXmlCData
	@JacksonXmlProperty(localName = "OutputMessage")
	@JsonInclude(Include.NON_NULL)
	private String outputMessage;

	public ErrorDetail()
	{
	}

	public ErrorDetail(String errorLevel, String inputMessage)
	{
		this.errorLevel = errorLevel;
		this.inputMessage = inputMessage;
	}

	public String getErrorLevel()
	{
		return errorLevel;
	}

	public void setErrorLevel(String errorLevel)
	{
		this.errorLevel = errorLevel;
	}

	public String getInputMessage()
	{
		return inputMessage;
	}

	public void setInputMessage(String inputMessage)
	{
		this.inputMessage = inputMessage;
	}

	public String getOutputMessage()
	{
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage)
	{
		this.outputMessage = outputMessage;
	}

}