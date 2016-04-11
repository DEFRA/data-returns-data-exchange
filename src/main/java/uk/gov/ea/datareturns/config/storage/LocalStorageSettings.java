package uk.gov.ea.datareturns.config.storage;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocalStorageSettings {
	@NotEmpty
	@JsonProperty("temporaryFolder")
	private String temporaryFolder;
	@NotEmpty
	@JsonProperty("persistentFolder")
	private String persistentFolder;

	@JsonProperty("cleanOnStartup")
	private boolean cleanOnStartup = true;

	public LocalStorageSettings() {
	}

	/**
	 * @return the temporaryFolder
	 */
	public String getTemporaryFolder() {
		return this.temporaryFolder;
	}

	/**
	 * @param temporaryFolder the temporaryFolder to set
	 */
	public void setTemporaryFolder(final String temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}

	/**
	 * @return the persistentFolder
	 */
	public String getPersistentFolder() {
		return this.persistentFolder;
	}

	/**
	 * @param persistentFolder the persistentFolder to set
	 */
	public void setPersistentFolder(final String persistentFolder) {
		this.persistentFolder = persistentFolder;
	}

	/**
	 * @return the cleanOnStartup
	 */
	public boolean isCleanOnStartup() {
		return this.cleanOnStartup;
	}

	/**
	 * @param cleanOnStartup the cleanOnStartup to set
	 */
	public void setCleanOnStartup(final boolean cleanOnStartup) {
		this.cleanOnStartup = cleanOnStartup;
	}
}