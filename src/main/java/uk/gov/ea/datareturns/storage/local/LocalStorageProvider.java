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
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Provides local storage for development use only (this WILL NOT work in a cluster)
 *
 * @author Sam Gardner-Dell
 */
public class LocalStorageProvider implements StorageProvider {
	private final File temporaryDir;
	private final File persistentDir;

	/**
	 *
	 */
	public LocalStorageProvider(final File temporaryDir, final File persistentDir) {
		this.temporaryDir = temporaryDir;
		this.persistentDir = persistentDir;
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
			throw new StorageException("Unable to decompress temp file.", e);
		}

		final File tempPropertiesFile = new File(this.temporaryDir, fileKey + ".properties");
		final Map<String, String> metadata = new LinkedHashMap<>();

		if (tempPropertiesFile.exists()) {
			final Properties props = new Properties();
			try (InputStream is = FileUtils.openInputStream(tempPropertiesFile)) {
				// Load the properties file
				props.load(is);
				// Store the properties data in the metadata map
				props.forEach((k, v) -> {
					metadata.put(Objects.toString(k), Objects.toString(v));
				});
			} catch (final IOException e) {
				throw new StorageException("Unable to read metadata properties", e);
			}
		}
		return new StoredFile(tempFile, metadata);
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
		props.putAll(metadata);

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
}