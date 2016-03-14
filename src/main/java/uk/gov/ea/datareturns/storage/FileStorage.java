package uk.gov.ea.datareturns.storage;

import static com.amazonaws.Protocol.HTTP;
import static com.amazonaws.Protocol.HTTPS;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import uk.gov.ea.datareturns.exception.application.DRFileKeyMismatchException;
import uk.gov.ea.datareturns.exception.system.DRFileDeleteException;
import uk.gov.ea.datareturns.exception.system.DRExternalServiceException;
import uk.gov.ea.datareturns.helper.CommonHelper;
import uk.gov.ea.datareturns.helper.FileUtilsHelper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

// TODO make generic instead of passing fixed env settings in
public class FileStorage
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorage.class);

	// TODO could all come from configuration file?
	public final static String BUCKET = "data-returns";
	public final static String FOLDER_FAILURE = "failure";
	public final static String FOLDER_SUCCESS = "success";
	public final static String SEPARATOR = "/";
	public final static String PROTOCOL_HTTP = "http";

	private Jedis fileKeyStorage;
	private ClientConfiguration s3Config;
	private String environment;

	public FileStorage(String environment, String redisHost, int redisPort)
	{
		this.environment = environment;
		this.fileKeyStorage = new Jedis(redisHost, redisPort);
	}

	public FileStorage(String environment, String redisHost, int redisPort, String s3ProxyType, String s3ProxyHost, int s3ProxyPort)
	{
		this(environment, redisHost, redisPort);

		this.s3Config = new ClientConfiguration();
		this.s3Config.setProtocol(getProtocolFromType(s3ProxyType));
		this.s3Config.setProxyHost(s3ProxyHost);
		this.s3Config.setProxyPort(s3ProxyPort);
	}


	public String saveInvalidFile(String fileLocation)
	{
		return storeFile(FOLDER_FAILURE, fileLocation);
	}

	public String saveValidFile(String fileLocation)
	{
		return storeFile(FOLDER_SUCCESS, fileLocation);
	}

	public String retrieveValidFileByKey(String fileKey, String saveFileLocation)
	{
		return retrieveFile(FOLDER_SUCCESS, fileKey, saveFileLocation);
	}

	// TODO needs a transaction
	private String storeFile(String outcome, String fileLocation)
	{
		String fileKey = generateFileKey();

		LOGGER.debug("File key '" + fileKey + "' generated for file '" + fileLocation + "'");

		// Non-local environments use S3
		if (!CommonHelper.isLocalEnvironment(environment))
		{
			LOGGER.debug("In Non-local environment");

			String fileName = FilenameUtils.getName(fileLocation);
			fileKeyStorage.set(fileKey, fileName);
			LOGGER.debug("File key '" + fileKey + "' saved in Redis with file name '" + fileName + "'");

			String key = makeFileDestinationPath(outcome, fileName);

			try
			{
				AmazonS3 s3client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider(), s3Config);

				LOGGER.debug("Saving file '" + fileName + "' to S3 Bucket '" + BUCKET + "' in folder '" + key + "'");
				s3client.putObject(new PutObjectRequest(BUCKET, key, new File(fileLocation)));
				LOGGER.debug("File saved successfully");

				FileUtilsHelper.deleteFile(fileLocation);
			} catch (AmazonServiceException ase)
			{
				throw new DRExternalServiceException(ase, "AWS failed to process putObject() request");
			} catch (AmazonClientException ace)
			{
				throw new DRExternalServiceException(ace, "General AWS communication failure");
			} catch (IOException e)
			{
				throw new DRFileDeleteException(e, "Unable to delete file to '" + fileLocation + "'");
			}
		} else
		{
			fileKeyStorage.set(fileKey, fileLocation);
			LOGGER.debug("File key '" + fileKey + "' saved in Redis with file location '" + fileLocation + "'");
		}

		LOGGER.debug("File stored successfully");

		return fileKey;
	}

	public String retrieveFile(String outcome, String fileKey, String saveFileLocation)
	{
		LOGGER.debug("Retrieving file location from Redis using file key '" + fileKey + "'");

		String fileLocation = fileKeyStorage.get(fileKey);

		if (fileLocation == null)
		{
			throw new DRFileKeyMismatchException("Unable to locate file using file key '" + fileKey + "'");
		}

		LOGGER.debug("Redis file key '" + fileKey + "' holds file location '" + fileLocation + "'");

		// Non-local environments use S3
		if (!CommonHelper.isLocalEnvironment(environment))
		{
			LOGGER.debug("In Non-local environment");

			// Note : only filename is stored in non-local env
			
			String key = makeFileDestinationPath(outcome, fileLocation);

			AmazonS3 s3client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider(), s3Config);
			LOGGER.debug("Retrieving file '" + fileLocation + "' from S3 Bucket '" + BUCKET + "'");
			try (
				S3Object s3object = s3client.getObject(new GetObjectRequest(BUCKET, key));
			)
			{

				LOGGER.debug("File '" + fileLocation + "' retrieved successfully");
				
				fileLocation = FileUtilsHelper.makeFullPath(saveFileLocation, fileLocation);
				
				FileUtilsHelper.saveFile(s3object.getObjectContent(), new File(fileLocation));
			} catch (AmazonServiceException ase)
			{
				throw new DRExternalServiceException(ase, "AWS failed to process getObject() request");
			} catch (AmazonClientException | IOException e)
			{
				throw new DRExternalServiceException(e, "General AWS communication failure");
			}
		}

		return fileLocation;
	}

	private static Protocol getProtocolFromType(String protocol)
	{
		return (PROTOCOL_HTTP.equalsIgnoreCase(protocol) ? HTTP : HTTPS);
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	private static String generateFileKey()
	{
		return UUID.randomUUID().toString();
	}

	private String makeFileDestinationPath(String result, String fileName)
	{
		return environment + SEPARATOR + result + SEPARATOR + fileName;
	}
}
