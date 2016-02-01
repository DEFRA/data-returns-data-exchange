package uk.gov.ea.datareturns.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class S3ProxySettings
{
	private String type;
	private String host;
	private String port;

	public S3ProxySettings()
	{
		this.type = "";
		this.host = "";
		this.port = "";
	}

	@JsonProperty
	public String getType()
	{
		return type;
	}

	@JsonProperty
	public void setType(String type)
	{
		this.type = type;
	}

	@JsonProperty
	public String getHost()
	{
		return this.host;
	}

	@JsonProperty
	public void setHost(String host)
	{
		this.host = host;
	}

	@JsonProperty
	public String getPort()
	{
		return this.port;
	}

	@JsonProperty
	public void setPort(String port)
	{
		this.port = port;
	}
}