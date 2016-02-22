package uk.gov.ea.datareturns.config;


/**
 * Holds S3 storage settings
 */
public class S3ProxySettings
{
	private String type;
	private String host;
	private int port;

	public S3ProxySettings(String type, String host, int port)
	{
		this.type = type;
		this.host = host;
		this.port = port;
	}

	public String getType()
	{
		return type;
	}

	public String getHost()
	{
		return this.host;
	}

	public int getPort()
	{
		return this.port;
	}
}