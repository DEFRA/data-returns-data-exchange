package uk.gov.ea.datareturns.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import uk.gov.ea.datareturns.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * The AmazonS3StorageProvider provides the Data Returns application with persistent storage to the Amazon S3 service
 * 
 * @author Sam Gardner-Dell
 */
public class AmazonS3StorageProvider implements StorageProvider {
	/** Class logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3StorageProvider.class);

	/** Message format for amazon service exception */
	private final static String FMT_SERVICE_EXCEPTION = "Amazon S3 service rejected an upload attempt. (HTTP Status: %d, AWS Error Code: %s, AWS Error Type: %s)";

	private final static int MEMORY_THRESHOLD = 1000000;

	/*  Assume file will compress to EXPECTED_COMPRESSION_MOD of original size for array initialisation purposes
	 	(to avoid array copy as much as poss).  testing has shown 80% to be a reasonable expectation for the data
	 	we store.
	*/
	private final static float EXPECTED_COMPRESSION_MOD = 0.8f;

	/** Amazon S3 client */
	private final AmazonS3 s3Client;
	private final TransferManager transferManager;
	private final AmazonS3Settings settings;

	/**
	 * Create a new Amazon storage provider.
	 * 
	 * Note that it is recommended to only use one storage provider for a given S3 endpoint to take advantage of
	 * connection pooling at other such goodness.
	 * 
	 * @param settings
	 */
	public AmazonS3StorageProvider(final AmazonS3Settings settings) {
		this.settings = settings;
		this.s3Client = new AmazonS3Client(this.settings.getCredentialProvider(), this.settings.getClientConfiguration());
		this.s3Client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(this.settings.isPathStyleAccess()));
		if (StringUtils.isNotBlank(this.settings.getEndpoint())) {
			this.s3Client.setEndpoint(this.settings.getEndpoint());
		}
		this.transferManager = new TransferManager(this.s3Client);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#storeTemporaryData(java.io.File)
	 */
	@Override
	public String storeTemporaryData(File file) throws StorageException {
		return store(settings.getTemporaryBucket(), file, null);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#retrieveTemporaryData(java.lang.String)
	 */
	@Override
	public StoredFile retrieveTemporaryData(String fileKey) throws StorageException {
		return retrieve(settings.getTemporaryBucket(), fileKey);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.StorageProvider#moveToAuditStore(java.lang.String, java.util.Map)
	 */
	@Override
	public String moveToAuditStore(String fileKey, Map<String, String> metadata) throws StorageException {
		// For now we're not going to change the file key used.
		move(settings.getTemporaryBucket(), fileKey, settings.getPersistentBucket(), fileKey, metadata);
		return fileKey;
	}
	
	
	/**
	 * @param bucketName
	 * @param region
	 * @return
	 */
	public String ensureBucketExists(final String bucketName, final Region region) {
		if (!(this.s3Client.doesBucketExist(bucketName))) {
			this.s3Client.createBucket(new CreateBucketRequest(bucketName, region));
		}
		return this.s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
	}

	/**
	 * @param bucketName
	 * @param file
	 * @param userMetadata
	 * @return
	 * @throws StorageException
	 */
	public String store(final String bucketName, final File file, final Map<String, String> userMetadata) throws StorageException {
		try (final S3StreamWrapper wrapper = compressFile(file)) {
			final String fileKey = StorageProvider.generateFileKey(file);

			try {
				final ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(wrapper.length);
				if (userMetadata != null) {
					metadata.setUserMetadata(userMetadata);
				}

				final PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, wrapper.stream,
						metadata);

				// Make the request to s3.
				this.s3Client.putObject(request);
			} catch (final AmazonServiceException ase) {
				final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
						ase.getErrorType().toString());
				throw new StorageException(message, ase);
			} catch (final AmazonClientException ace) {
				final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
						+ ace.getMessage();
				throw new StorageException(message, ace);
			}
			return fileKey;
		}
	}

	/**
	 * Uses multi-part file uploads to S3 - useful with large files (>=100MB) as it uploads chunks of data
	 * in parallel.  If a chunk fails then only that chunk has to be re-uploaded rathen than having to try
	 * the entire upload again.  DO NOT use for small files as this is horridly inefficient.
	 *
	 * @param file
	 * @param userMetadata
	 * @return
	 * @throws StorageException
	 */
	public String storeMultipart(final String bucketName, final File file, final Map<String, String> userMetadata) throws StorageException {
		try (final S3StreamWrapper wrapper = compressFile(file)) {
			final String fileKey = StorageProvider.generateFileKey(file);

			try {
				final ObjectMetadata metadata = new ObjectMetadata();
				if (userMetadata != null) {
					metadata.setUserMetadata(userMetadata);
				}
				metadata.setContentLength(wrapper.length);
				final PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, wrapper.stream,
						metadata);

				final Upload upload = this.transferManager.upload(request);
				upload.waitForCompletion();
				// Make the request to s3.
				this.s3Client.putObject(request);
			} catch (final AmazonServiceException ase) {
				final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
						ase.getErrorType().toString());
				throw new StorageException(message, ase);
			} catch (final AmazonClientException ace) {
				final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
						+ ace.getMessage();
				throw new StorageException(message, ace);
			} catch (final InterruptedException e) {
				final String message = "Upload to S3 thread interrupted";
				throw new StorageException(message, e);
			}
			return fileKey;
		}
	}

	/**
	 * Update the metadata stored against the given bucket and file key
	 * 
	 * @param bucketName the bucket name of the bucket containing the desired file key
	 * @param fileKey the key of the file whose metadata should be updated
	 * @param userMetadata the new user metadata to store along with the file.
	 * @throws StorageException if the metadata could not be updated (e.g. due to a connection issue)
	 */
	public void updateMetadata(final String bucketName, final String fileKey, final Map<String, String> userMetadata) throws StorageException {
		try {
			final ObjectMetadata metadata = new ObjectMetadata();
			metadata.setUserMetadata(userMetadata);
			final CopyObjectRequest request = new CopyObjectRequest(bucketName, fileKey, bucketName, fileKey)
					.withNewObjectMetadata(metadata);
			// Make the request to s3.
			this.s3Client.copyObject(request);
		} catch (final AmazonServiceException ase) {
			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
					ase.getErrorType().toString());
			throw new StorageException(message, ase);
		} catch (final AmazonClientException ace) {
			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
					+ ace.getMessage();
			throw new StorageException(message, ace);
		}
	}

	/**
	 * Move the specified file from the source to the destination, optionally including updated user metadata in the
	 * destination object
	 * 
	 * @param srcBucket the bucket containing the file to be moved
	 * @param srcKey the key to identify the file to be moved
	 * @param dstBucket the bucket the file should be moved to
	 * @param dstKey the new key used to identify the file in the destination
	 * @param newMetadata new metadata to store for the file (if null, then metadata shall be unaffected by the move)
	 * @throws StorageException if the file couldn't be moved (e.g. due to a connection issue etc)
	 */
	public void move(final String srcBucket, final String srcKey, final String dstBucket, final String dstKey,
			final Map<String, String> newMetadata) throws StorageException {
		try {
			final CopyObjectRequest request = new CopyObjectRequest(srcBucket, srcKey, dstBucket, dstKey);

			if (newMetadata != null) {
				final ObjectMetadata metadata = new ObjectMetadata();
				metadata.setUserMetadata(newMetadata);
				request.setNewObjectMetadata(metadata);
			}
			// Make the request to s3.
			this.s3Client.copyObject(request);

			final DeleteObjectRequest deleteRequest = new DeleteObjectRequest(srcBucket, srcKey);
			this.s3Client.deleteObject(deleteRequest);
		} catch (final AmazonServiceException ase) {
			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
					ase.getErrorType().toString());
			throw new StorageException(message, ase);
		} catch (final AmazonClientException ace) {
			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
					+ ace.getMessage();
			throw new StorageException(message, ace);
		}
	}

	/**
	 * Retrieve a stored file for the specified bucket name and key
	 * 
	 * @param bucketName the name of the bucket containing the file to be retrieved
	 * @param fileKey the key which identifies the file to be retrieved
	 * @return a {@link StoredFile} object containing
	 * @throws StorageException
	 */
	public StoredFile retrieve(final String bucketName, final String fileKey) throws StorageException {
		try (
				final S3Object s3object = this.s3Client.getObject(new GetObjectRequest(bucketName, fileKey));
				final InputStream in = s3object.getObjectContent();
				final GZIPInputStream gzipStream = new GZIPInputStream(in)) {
			final File tempFile = File.createTempFile("data-returns-", null);
			FileUtils.copyInputStreamToFile(gzipStream, tempFile);

			return  new StoredFile(tempFile, s3object.getObjectMetadata().getUserMetadata());
		} catch (final AmazonServiceException ase) {
			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
					ase.getErrorType().toString());

			if (ase.getErrorCode().equals("NoSuchKey")) {
				throw new StorageKeyMismatchException(message, ase);
			}
			throw new StorageException(message, ase);
		} catch (final AmazonClientException ace) {
			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
					+ ace.getMessage();
			throw new StorageException(message, ace);
		} catch (final IOException e) {
			throw new StorageException("AmazonS3StorageProvider:: Unable to retrieve file from S3: " + e.getMessage(),
					e);
		}
	}

	/**
	 * Apply gzip compression to a file automatically determining the most efficient method of compression
	 *
	 * @param file the file to be compressed
	 * @return a wrapper providing details about the compressed file
	 * @throws StorageException if an error occurs while attempting to read/write a file
	 */
	private static final S3StreamWrapper compressFile(final File file) throws StorageException {
		if (file.length() < MEMORY_THRESHOLD) {
			// Small file, read this in memory
			return compressInSystemMemory(file);
		}
		return compressOnDisk(file);
	}

	/**
	 * Apply gzip compression to a file using a system memory buffer
	 *
	 * @param file the file to be compressed
	 * @return a wrapper providing details about the compressed file
	 * @throws StorageException if an error occurs while attempting to read/write a file
	 */
	private static final S3StreamWrapper compressInSystemMemory(final File file) throws StorageException {
		final int bufferSize = Math.round(file.length() * EXPECTED_COMPRESSION_MOD);
		byte[] data = {};
		try (
				ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
				GZIPOutputStream gos = new GZIPOutputStream(bos)) {
			FileUtils.copyFile(file, gos);
			// Must close the gzip outputstream here to force all data into the byte array
			IOUtils.closeQuietly(gos);
			data = bos.toByteArray();
			final InputStream bis = new ByteArrayInputStream(data);
			return new S3StreamWrapper(bis, data.length, null);
		} catch (final IOException e) {
			throw new StorageException("AmazonS3StorageProvider:: Unable to compressed file in memory", e);
		}
	}

	/**
	 * Apply gzip compression to a file using the filesystem
	 *
	 * @param file the file to be compressed
	 * @return a wrapper providing details about the compressed file
	 * @throws StorageException if an error occurs while attempting to read/write a file
	 */
	@SuppressWarnings("resource")
	private static final S3StreamWrapper compressOnDisk(final File file) throws StorageException {
		try {
			// Create a gzipped version of the file in the temporary folder.
			final File gzipFile = File.createTempFile("data-returns", StorageProvider.getGzipSuffix(file));
			try (
					final FileOutputStream fos = new FileOutputStream(file);
					final GZIPOutputStream gzipStream = new GZIPOutputStream(fos)) {
				FileUtils.copyFile(file, gzipStream);
			}
			final FileInputStream fis = FileUtils.openInputStream(gzipFile);
			return new S3StreamWrapper(fis, gzipFile.length(), () -> {
				// Delete the temporary gzip file
				if (!FileUtils.deleteQuietly(gzipFile)) {
					LOGGER.error("AmazonS3StorageProvider:: Unable to delete temporary GZIP");
					gzipFile.deleteOnExit();
				}
			});
		} catch (final IOException e) {
			throw new StorageException("AmazonS3StorageProvider:: Unable to create compressed version of file", e);
		}
	}

	/**
	 * Class used internally to manage streams
	 * 
	 * @author Sam Gardner-Dell
	 */
	private static class S3StreamWrapper implements Closeable {
		private final InputStream stream;
		private final long length;
		private final Callback closeHandler;

		public S3StreamWrapper(final InputStream inputStream, final long inputStreamLength, final Callback releaseHandler) {
			this.stream = inputStream;
			this.length = inputStreamLength;
			this.closeHandler = releaseHandler;
		}

		@Override
		public void close() {
			IOUtils.closeQuietly(this.stream);
			if (this.closeHandler != null) {
				this.closeHandler.callback();
			}
		}
	}

	/**
	 * Simple callback interface (marked with @FunctionalInterface so that we can use lambda!)
	 * 
	 * @author Sam Gardner-Dell
	 */
	@FunctionalInterface
	private static interface Callback {
		void callback();
	}
}