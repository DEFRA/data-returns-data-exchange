package uk.gov.ea.datareturns.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class S3ProxySettings
{
	private String type;
	private String host;
	private int port;

	public S3ProxySettings()
	{
		this.type = "";
		this.host = "";
		this.port = 0;
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
	public int getPort()
	{
		return this.port;
	}

	@JsonProperty
	public void setPort(int port)
	{
		this.port = port;
	}
}