/**
 * 
 */
package uk.gov.ea.datareturns.storage.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;

import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Provides local storage for development use only (this WILL NOT work in a cluster)
 * 
 * @author Sam Gardner-Dell
 */
public class LocalStorageProvider implements StorageProvider {
	private File temporaryDir;
	private File persistentDir;

	/**
	 * 
	 */
	public LocalStorageProvider(File temporaryDir, File persistentDir) {
		this.temporaryDir = temporaryDir;
		this.persistentDir = persistentDir;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#storeTemporaryData(java.io.File)
	 */
	@Override
	public String storeTemporaryData(File file) throws StorageException {
		final String fileKey = StorageProvider.generateFileKey(file);
		final File tempFile = new File(temporaryDir, fileKey);

		try (OutputStream fos = new FileOutputStream(tempFile);
				GZIPOutputStream gos = new GZIPOutputStream(fos)) {
			FileUtils.copyFile(file, gos);
		} catch (IOException e) {
			throw new StorageException("Unable to save file to temporary storage", e);
		}
		return fileKey;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#retrieveTemporaryData(java.lang.String)
	 */
	@Override
	public StoredFile retrieveTemporaryData(String fileKey) throws StorageException {
		File compressedTempFile = new File(temporaryDir, fileKey);
		File tempFile = null;
		
		try (
				final InputStream in = FileUtils.openInputStream(compressedTempFile);
				final GZIPInputStream gzipStream = new GZIPInputStream(in)) {
			tempFile = File.createTempFile("data-returns-", null);
			FileUtils.copyInputStreamToFile(gzipStream, tempFile);
		} catch (IOException e) {
			throw new StorageException("Unable to decompress temp file.", e);
		}
		
		
		File tempPropertiesFile = new File(temporaryDir, fileKey + ".properties");
		final Map<String, String> metadata = new LinkedHashMap<>();
		
		if (tempPropertiesFile.exists()) {
			Properties props = new Properties();
			try (InputStream is = FileUtils.openInputStream(tempPropertiesFile)) {
				// Load the properties file
				props.load(is);
				// Store the properties data in the metadata map
				props.forEach((k, v) -> { metadata.put(String.valueOf(k), String.valueOf(v)); });
			} catch (IOException e) {
				throw new StorageException("Unable to read metadata properties", e);
			}
		}
		return new StoredFile(tempFile, metadata);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#moveToAuditStore(java.lang.String, java.util.Map)
	 */
	@Override
	public String moveToAuditStore(String fileKey, Map<String, String> metadata) throws StorageException {
		File sourceFile = new File(temporaryDir, fileKey);
		
		File persistentFile = new File(persistentDir, fileKey);
		File persistentPropertiesFile = new File(persistentDir, fileKey + ".properties");
		
		Properties props = new Properties();
		props.putAll(metadata);
		
		try (OutputStream pout = FileUtils.openOutputStream(persistentPropertiesFile)) {
			// Write out the metadata as as properties file
			props.store(pout, null);
			// Move the temporary file to the persistent store
			FileUtils.moveFile(sourceFile, persistentFile);
		} catch (IOException e) {
			throw new StorageException("Unable to move temporary data to persistent store", e);
		}
		return fileKey;
	}
}