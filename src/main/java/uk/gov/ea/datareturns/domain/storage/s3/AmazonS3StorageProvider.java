package uk.gov.ea.datareturns.domain.storage.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import uk.gov.ea.datareturns.domain.storage.StorageException;
import uk.gov.ea.datareturns.domain.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;

/**
 * The AmazonS3StorageProvider provides the Data Returns application with persistent storage to the Amazon S3 service
 *
 * @author Sam Gardner-Dell
 */
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class AmazonS3StorageProvider implements StorageProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3StorageProvider.class);

	/** Message format for amazon service exception */
	private final static String FMT_SERVICE_EXCEPTION = "Amazon S3 service rejected an upload attempt. (HTTP Status: %d, AWS Error Code: %s, AWS Error Type: %s)";

	/** Amazon S3 client */
	private final AmazonS3 s3Client;

	//	private final TransferManager transferManager;

	private final AmazonS3Configuration settings;

	/**
	 * Create a new Amazon storage provider.
	 *
	 * Note that it is recommended to only use one storage provider for a given S3 endpoint to take advantage of
	 * connection pooling at other such goodness.
	 *
	 * @param settings the Amazon S3 configuration settings
	 */
	@Inject
	public AmazonS3StorageProvider(final AmazonS3Configuration settings) {
		LOGGER.info("Initialising AWS S3 Storage Provider");
		this.settings = settings;
		this.s3Client = new AmazonS3Client(this.settings.getCredentialProvider(), this.settings.getClientConfiguration());

		final S3ClientOptions clientOptions = S3ClientOptions
				.builder()
				.setPathStyleAccess(this.settings.isPathStyleAccess())
				.build();

		this.s3Client.setS3ClientOptions(clientOptions);
		if (StringUtils.isNotBlank(this.settings.getEndpoint())) {
			this.s3Client.setEndpoint(this.settings.getEndpoint());
		}
		//		this.transferManager = new TransferManager(this.s3Client);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.storage.StorageProvider#storeTemporaryData(java.io.File)
	 */
	@Override
	public String storeTemporaryData(final File file) throws StorageException {
		return store(this.settings.getTemporaryBucket(), file, null);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.storage.StorageProvider#retrieveTemporaryData(java.lang.String)
	 */
	@Override
	public StoredFile retrieveTemporaryData(final String fileKey) throws StorageException {
		return retrieve(this.settings.getTemporaryBucket(), fileKey);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.storage.StorageProvider#moveToAuditStore(java.lang.String, java.util.Map)
	 */
	@Override
	public String moveToAuditStore(final String fileKey, final Map<String, String> metadata) throws StorageException {
		// For now we're not going to change the file key used.
		move(this.settings.getTemporaryBucket(), fileKey, this.settings.getPersistentBucket(), fileKey, metadata);
		return fileKey;
	}

	/**
	 * Store a new file and associated metadata in the given bucket name
	 *
	 * @param bucketName the name of the bucket the file should be stored in
	 * @param file the file that should be stored
	 * @param userMetadata the metadata to be associated with the file
	 * @return the key which should be used for future references to the file stored
	 * @throws StorageException if an error occurred while attempting to store the file
	 */
	public String store(final String bucketName, final File file, final Map<String, String> userMetadata) throws StorageException {
		final String fileKey = StorageProvider.generateFileKey(file);

		try (InputStream is = FileUtils.openInputStream(file)) {
			final ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.length());
			if (userMetadata != null) {
				metadata.setUserMetadata(userMetadata);
			}

			final PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, is, metadata);

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
		} catch (final IOException e) {
			throw new StorageException("Unable to read file", e);
		}
		return fileKey;
	}

	//	/**
	//	 * Uses multi-part file uploads to S3 - useful with large files (>=100MB) as it uploads chunks of data
	//	 * in parallel.  If a chunk fails then only that chunk has to be re-uploaded rather than having to try
	//	 * the entire upload again.  DO NOT use for small files as this is horridly inefficient.
	//	 *
	//	 * @param file
	//	 * @param userMetadata
	//	 * @return
	//	 * @throws StorageException
	//	 */
	//	public String storeMultipart(final String bucketName, final File file, final Map<String, String> userMetadata) throws StorageException {
	//		final String fileKey = StorageProvider.generateFileKey(file);
	//
	//		try (InputStream is = FileUtils.openInputStream(file)) {
	//			final ObjectMetadata metadata = new ObjectMetadata();
	//			if (userMetadata != null) {
	//				metadata.setUserMetadata(userMetadata);
	//			}
	//			metadata.setContentLength(file.length());
	//			final PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, is, metadata);
	//
	//			final Upload upload = this.transferManager.upload(request);
	//			upload.waitForCompletion();
	//			// Make the request to s3.
	//			this.s3Client.putObject(request);
	//		} catch (final AmazonServiceException ase) {
	//			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
	//					ase.getErrorType().toString());
	//			throw new StorageException(message, ase);
	//		} catch (final AmazonClientException ace) {
	//			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
	//					+ ace.getMessage();
	//			throw new StorageException(message, ace);
	//		} catch (final InterruptedException e) {
	//			final String message = "Upload to S3 thread interrupted";
	//			throw new StorageException(message, e);
	//		} catch (final IOException e) {
	//			throw new StorageException("Unable to read file", e);
	//		}
	//		return fileKey;
	//	}

	//	/**
	//	 * Update the metadata stored against the given bucket and file key
	//	 *
	//	 * @param bucketName the bucket name of the bucket containing the desired file key
	//	 * @param fileKey the key of the file whose metadata should be updated
	//	 * @param userMetadata the new user metadata to store along with the file.
	//	 * @throws StorageException if the metadata could not be updated (e.g. due to a connection issue)
	//	 */
	//	public void updateMetadata(final String bucketName, final String fileKey, final Map<String, String> userMetadata)
	//			throws StorageException {
	//		try {
	//			final ObjectMetadata metadata = new ObjectMetadata();
	//			metadata.setUserMetadata(userMetadata);
	//			final CopyObjectRequest request = new CopyObjectRequest(bucketName, fileKey, bucketName, fileKey)
	//					.withNewObjectMetadata(metadata);
	//			// Make the request to s3.
	//			this.s3Client.copyObject(request);
	//		} catch (final AmazonServiceException ase) {
	//			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
	//					ase.getErrorType().toString());
	//			throw new StorageException(message, ase);
	//		} catch (final AmazonClientException ace) {
	//			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
	//					+ ace.getMessage();
	//			throw new StorageException(message, ace);
	//		}
	//	}

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
	 * @throws StorageException if a problem occurred attempting to retrieve the file
	 */
	public StoredFile retrieve(final String bucketName, final String fileKey) throws StorageException {
		try (final S3Object s3object = this.s3Client.getObject(new GetObjectRequest(bucketName, fileKey));
				final InputStream in = s3object.getObjectContent()) {
			final File tempFile = File.createTempFile("data-returns-", null);
			FileUtils.copyInputStreamToFile(in, tempFile);

			return new StoredFile(tempFile, s3object.getObjectMetadata().getUserMetadata());
		} catch (final AmazonServiceException ase) {
			final String message = String.format(FMT_SERVICE_EXCEPTION, ase.getStatusCode(), ase.getErrorCode(),
					ase.getErrorType().toString());

			if (ase.getErrorCode().equals("NoSuchKey")) {
				throw new StorageKeyMismatchException("The file for the specified key cannot be found.");
			}
			throw new StorageException(message, ase);
		} catch (final AmazonClientException ace) {
			final String message = "An error occurred that prevented the Amazon S3 service from being contacted.  Cause: "
					+ ace.getMessage();
			throw new StorageException(message, ace);
		} catch (final IOException e) {
			throw new StorageException("AmazonS3StorageProvider:: Unable to retrieve file from S3: " + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.storage.StorageProvider#healthy()
	 */
	@Override
	public boolean healthy() throws StorageException {
		try {
			return this.s3Client.doesBucketExist(this.settings.getTemporaryBucket())
					&& this.s3Client.doesBucketExist(this.settings.getPersistentBucket());
		} catch (final AmazonServiceException e) {
			throw new StorageException("Health check failed with exception", e);
		}
	}
}