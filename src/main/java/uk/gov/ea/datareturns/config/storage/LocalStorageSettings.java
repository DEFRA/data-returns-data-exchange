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
		return temporaryFolder;
	}

	/**
	 * @param temporaryFolder the temporaryFolder to set
	 */
	public void setTemporaryFolder(String temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}

	/**
	 * @return the persistentFolder
	 */
	public String getPersistentFolder() {
		return persistentFolder;
	}

	/**
	 * @param persistentFolder the persistentFolder to set
	 */
	public void setPersistentFolder(String persistentFolder) {
		this.persistentFolder = persistentFolder;
	}

	/**
	 * @return the cleanOnStartup
	 */
	public boolean isCleanOnStartup() {
		return cleanOnStartup;
	}

	/**
	 * @param cleanOnStartup the cleanOnStartup to set
	 */
	public void setCleanOnStartup(boolean cleanOnStartup) {
		this.cleanOnStartup = cleanOnStartup;
	}
}