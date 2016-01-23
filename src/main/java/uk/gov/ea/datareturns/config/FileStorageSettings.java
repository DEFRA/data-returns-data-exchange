package uk.gov.ea.datareturns.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author adrianharrison
 * Holds file storage settings from configuration file
 */
public class FileStorageSettings
{
	@Valid
	@NotNull
	private RedisSettings redisSettings = new RedisSettings();

	@JsonProperty("redis")
	public RedisSettings getRedisSettings()
	{
		return redisSettings;
	}

	@JsonProperty("redis")
	public void setRedisSettings(RedisSettings fileStorageSettings)
	{
		this.redisSettings = fileStorageSettings;
	}
}