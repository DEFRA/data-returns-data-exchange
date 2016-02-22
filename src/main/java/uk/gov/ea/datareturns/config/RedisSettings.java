package uk.gov.ea.datareturns.config;

/**
 * Redis settings
 */
public class RedisSettings
{
	private String host;
	private int port;

	public RedisSettings(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}
}