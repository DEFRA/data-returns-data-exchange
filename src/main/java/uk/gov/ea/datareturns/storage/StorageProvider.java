/**
 * 
 */
package uk.gov.ea.datareturns.storage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sam Gardner-Dell
 */
public interface StorageProvider {
	/**
	 * Used to persist temporary working data so that it is accessible to all nodes within the cluster
	 * 
	 * @param file the file to be stored
	 * @return a key identifier unique to the file
	 * @throws StorageException if a problem occurs which prevents the file from being stored
	 */
	String storeTemporaryData(File file) throws StorageException;
	
	
	/**
	 * Retrieves temporary working data from store
	 * 
	 * @param fileKey the key identifier for the file to be retrieved
	 * @return a {@link StoredFile} object providing all stored file information
	 * @throws StorageException if a problem occurs which prevents the file from being retrieved 
	 */
	StoredFile retrieveTemporaryData(String fileKey) throws StorageException;
	
	/**
	 * Stored temporary working data in a persistent store (for audit purposes) and associates the data provided
	 * in the metadata map with the file
	 * 
	 * @param fileKey the key identifier used to identify the file 
	 * @param metadata the new metadata to be stored against the file in the permanent store area.
	 * @return the new key identifier for the file in the permanent store area.
	 * @throws StorageException
	 */
	String moveToAuditStore(String fileKey, Map<String, String> metadata) throws StorageException;
	
	

	/**
	 * Generate a file key to identify this file on S3.
	 *
	 * @param file the {@link File} for which a key shall be created for
	 * @return a {@link String} containing the file key used to store files in S3
	 */
	static String generateFileKey(final File file) {
		// Generate a 4 digit hex string to aid partition randomness
		// See http://docs.aws.amazon.com/AmazonS3/latest/dev/request-rate-perf-considerations.html
		final String partitioning = String.format("%04x", RandomUtils.nextInt(0x0000, 0xffff));
		final String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'-'HH-mm"));
		final String uuid = UUID.randomUUID().toString();
		return partitioning + "-" + datePart + "-" + uuid + getGzipSuffix(file);
	}


	/**
	 * Gets a gzip suffix suitable for a file.
	 *
	 * E.g. /tmp/myfile.txt  -> .txt.gz
	 *      /tmp/another.csv -> .csv.gz
	 *      /tmp/anotherex   -> .gz
	 *
	 * @param file the {@link File} to create the suffix for
	 * @return a file suffix as a {@link String} - note that this will start with a period (.)
	 */
	static String getGzipSuffix(final File file) {
		final String oldSuffix = FilenameUtils.getExtension(file.getName());
		final String newSuffix = StringUtils.isNotBlank(oldSuffix) ? "." + oldSuffix + ".gz" : ".gz";
		return newSuffix;
	}
	
	public static class StoredFile {
		private final File file;
		private final Map<String, String> metadata;
		
		public StoredFile(File file, Map<String, String> metadata) {
			super();
			this.file = file;
			this.metadata = metadata;
		}
		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}
		/**
		 * @return the metadata
		 */
		public Map<String, String> getMetadata() {
			return metadata;
		}
	}
}