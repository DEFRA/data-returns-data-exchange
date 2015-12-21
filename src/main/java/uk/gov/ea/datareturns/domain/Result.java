package uk.gov.ea.datareturns.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Result
{
	@JacksonXmlProperty(localName = "Id")
	private String id;

	@JacksonXmlProperty(localName = "Value")
	@JsonInclude(Include.NON_NULL)
	private String value;

	public Result()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}