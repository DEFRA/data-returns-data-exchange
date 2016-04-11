/**
 *
 */
package uk.gov.ea.datareturns.config.storage;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Top-level storage settings element (storage)
 *
 * @author Sam Gardner-Dell
 */
public class StorageSettings {
	@Valid
	@JsonProperty("local")
	private LocalStorageSettings localConfig;

	@Valid
	@JsonProperty("s3")
	private S3StorageSettings s3Config;

	/**
	 *
	 */
	public StorageSettings() {
	}

	/**
	 * @return the localConfig
	 */
	public LocalStorageSettings getLocalConfig() {
		return this.localConfig;
	}

	/**
	 * @param localConfig the localConfig to set
	 */
	public void setLocalConfig(final LocalStorageSettings localConfig) {
		this.localConfig = localConfig;
	}

	/**
	 * @return the s3Config
	 */
	public S3StorageSettings getS3Config() {
		return this.s3Config;
	}

	/**
	 * @param s3Config the s3Config to set
	 */
	public void setS3Config(final S3StorageSettings s3Config) {
		this.s3Config = s3Config;
	}

	/**
	 * Determine the storage type that has been configured
	 *
	 * @return
	 */
	@JsonIgnore
	public StorageType getStorageType() {
		StorageType type = null;
		if (this.localConfig != null) {
			type = StorageType.LOCAL;
		} else if (this.s3Config != null) {
			type = StorageType.S3;
		}
		return type;
	}

	/**
	 * Simple enumeration of available storage types
	 *
	 * @author Sam Gardner-Dell
	 */
	public enum StorageType {
		LOCAL, S3;
	}
}