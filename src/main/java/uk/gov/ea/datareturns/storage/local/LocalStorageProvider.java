/**
 *
 */
package uk.gov.ea.datareturns.storage.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.config.storage.LocalStorageConfiguration;
import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Provides local storage for development use only (this WILL NOT work in a cluster)
 *
 * @author Sam Gardner-Dell
 */
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LocalStorageProvider implements StorageProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageProvider.class);

	private final File temporaryDir;

	private final File persistentDir;

	@Inject
	public LocalStorageProvider(final LocalStorageConfiguration settings) {
		LOGGER.info("Initialising Local Storage Provider");

		this.temporaryDir = settings.getTemporaryFolder();
		this.persistentDir = settings.getPersistentFolder();

		if (settings.isCleanOnStartup()) {
			try {
				for (final File dir : new File[] { this.temporaryDir, this.persistentDir }) {
					FileUtils.forceMkdir(dir);
					FileUtils.cleanDirectory(dir);
				}
			} catch (final IOException e) {
				LOGGER.warn("Error preparing local stroage area", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#storeTemporaryData(java.io.File)
	 */
	@Override
	public String storeTemporaryData(final File file) throws StorageException {
		final String fileKey = StorageProvider.generateFileKey(file);
		final File tempFile = new File(this.temporaryDir, fileKey);

		try (final OutputStream fos = FileUtils.openOutputStream(tempFile)) {
			FileUtils.copyFile(file, fos);
		} catch (final IOException e) {
			throw new StorageException("Unable to save file to temporary storage", e);
		}
		return fileKey;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#retrieveTemporaryData(java.lang.String)
	 */
	@Override
	public StoredFile retrieveTemporaryData(final String fileKey) throws StorageException {
		final File compressedTempFile = new File(this.temporaryDir, fileKey);
		File tempFile = null;

		if (!compressedTempFile.exists()) {
			throw new StorageKeyMismatchException("The file for the specified key cannot be found.");
		}

		try (final InputStream in = FileUtils.openInputStream(compressedTempFile)) {
			tempFile = File.createTempFile("data-returns-", null);
			FileUtils.copyInputStreamToFile(in, tempFile);
		} catch (final IOException e) {
			throw new StorageException("Unable to read temp file.", e);
		}
		return new StoredFile(tempFile, new LinkedHashMap<>());
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#moveToAuditStore(java.lang.String, java.util.Map)
	 */
	@Override
	public String moveToAuditStore(final String fileKey, final Map<String, String> metadata) throws StorageException {
		final File sourceFile = new File(this.temporaryDir, fileKey);

		final File persistentFile = new File(this.persistentDir, fileKey);
		final File persistentPropertiesFile = new File(this.persistentDir, fileKey + ".properties");

		final Properties props = new Properties();
		if (metadata != null) {
			props.putAll(metadata);
		}

		try (OutputStream pout = FileUtils.openOutputStream(persistentPropertiesFile)) {
			// Write out the metadata as as properties file
			props.store(pout, null);
			// Move the temporary file to the persistent store
			FileUtils.moveFile(sourceFile, persistentFile);
		} catch (final IOException e) {
			throw new StorageException("Unable to move temporary data to persistent store", e);
		}
		return fileKey;
	}

	@Override
	public boolean healthy() throws StorageException {
		return this.temporaryDir.canWrite() && this.persistentDir.canWrite();
	}
}