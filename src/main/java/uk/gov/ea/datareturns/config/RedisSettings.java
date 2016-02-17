package uk.gov.ea.datareturns.config;

/**
 * Redis settings
 */
public class RedisSettings
{
	private String host;
	private String port;

	public RedisSettings(String host, String port)
	{
		this.host = host;
		this.port = port;
	}

	public String getHost()
	{
		return host;
	}

	public String getPort()
	{
		return port;
	}
}