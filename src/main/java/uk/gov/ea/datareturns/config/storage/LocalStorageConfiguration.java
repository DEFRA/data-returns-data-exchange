package uk.gov.ea.datareturns.config.storage;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="storage.local")
public class LocalStorageConfiguration {
	private File temporaryFolder;
	private File persistentFolder;
	private boolean cleanOnStartup = true;

	/**
	 * @return the temporaryFolder
	 */
	public File getTemporaryFolder() {
		return this.temporaryFolder;
	}

	/**
	 * @param temporaryFolder the temporaryFolder to set
	 */
	public void setTemporaryFolder(final File temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}

	/**
	 * @return the persistentFolder
	 */
	public File getPersistentFolder() {
		return this.persistentFolder;
	}

	/**
	 * @param persistentFolder the persistentFolder to set
	 */
	public void setPersistentFolder(final File persistentFolder) {
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