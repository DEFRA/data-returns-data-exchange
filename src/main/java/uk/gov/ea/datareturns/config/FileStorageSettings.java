package uk.gov.ea.datareturns.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author adrianharrison
 * Holds file storage settings
 */
public class FileStorageSettings
{
	@Valid
	@NotNull
	private RedisSettings redisSettings;

	public RedisSettings getRedisSettings()
	{
		return redisSettings;
	}

	public void setRedisSettings(String redisHost, String redisPort)
	{
		this.redisSettings = new RedisSettings(redisHost, redisPort);
	}
}