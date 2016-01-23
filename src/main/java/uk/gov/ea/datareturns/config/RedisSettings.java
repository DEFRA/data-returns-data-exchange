package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author adrianharrison
 * Holds redis settings from configuration file
 */
public class RedisSettings
{
	@NotEmpty
	private String host;

	@NotEmpty
	private String port;

	public void setPort(String port)
	{
		this.port = port;
	}

	public RedisSettings()
	{
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getPort()
	{
		return port;
	}
}