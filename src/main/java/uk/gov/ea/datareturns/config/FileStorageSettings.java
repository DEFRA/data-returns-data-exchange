package uk.gov.ea.datareturns.config;

/**
 * Holds file storage settings
 */
public class FileStorageSettings
{
	private RedisSettings redisSettings;
	private S3ProxySettings s3ProxySettings;

	public RedisSettings getRedisSettings()
	{
		return redisSettings;
	}

	public void setRedisSettings(String redisHost, int redisPort)
	{
		this.redisSettings = new RedisSettings(redisHost, redisPort);
	}

	public S3ProxySettings getS3ProxySettings()
	{
		return s3ProxySettings;
	}

	public void setS3ProxySettings(String type, String host, int port)
	{
		this.s3ProxySettings = new S3ProxySettings(type, host, port);
	}
}