package uk.gov.ea.datareturns.storage;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class FileStorage
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorage.class);

	public final static String ENV_LOCAL = "local";
	public final static String ENV_DEV = "dev";

	public final static String BUCKET_ROOT = "data-returns";
	public final static String BUCKET_DEV = "dev";
	public final static String BUCKET_RESULT_FAILURE = "failure";
	public final static String BUCKET_RESULT_SUCCESS = "success";
	public final static String BUCKET_PATH_SEPARATOR = "/";

	private Jedis storage;
	private String environment;
	private String host;
	private int port;

	public FileStorage(String environment, String host, int port)
	{
		this.environment = environment;
		this.host = host;
		this.port = port;
		this.storage = new Jedis(this.host, this.port);
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public Jedis getStorage()
	{
		return storage;
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	public String generateFileKey()
	{
		return UUID.randomUUID().toString();
	}

	public String saveFailedFile(String fileLocation)
	{
		return saveFile(BUCKET_RESULT_FAILURE, fileLocation);
	}

	public String saveSuccessFile(String fileLocation)
	{
		return saveFile(BUCKET_RESULT_SUCCESS, fileLocation);
	}

	// TODO transaction
	public String saveFile(String result, String fileLocation)
	{
		String key = generateFileKey();

		LOGGER.debug("File key generated for file '" + fileLocation + "' = '" + key);

		this.storage.set(key, fileLocation);

		LOGGER.debug("File key '" + key + "'saved in Redis");

		// Non-local environments use S3 buckets
		if (ENV_LOCAL.equalsIgnoreCase(environment))
		{
			String bucketName = getFullBucketName(result);

			LOGGER.debug("Saving file to S3 Bucket '" + bucketName + "'");

			LOGGER.debug("Creating EnvironmentVariableCredentialsProvider() client");
			AmazonS3 s3client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
			LOGGER.debug("Client created = " + s3client);

			try
			{
//				System.out.println("Uploading a new object to S3 from a file\n");
				File file = new File(fileLocation);
				LOGGER.debug("before S3 PUT to " + bucketName);
				s3client.putObject(new PutObjectRequest(bucketName, key, file));
				for(Bucket b: s3client.listBuckets())
				{
					LOGGER.debug(b.getName().toString());
				}
				LOGGER.debug("after S3 PUT");

			} catch (AmazonServiceException ase)
			{
				System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
						+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
			} catch (AmazonClientException ace)
			{
				System.out.println("Caught an AmazonClientException, which " + "means the client encountered " + "an internal error while trying to "
						+ "communicate with S3, " + "such as not being able to access the network.");
				System.out.println("Error Message: " + ace.getMessage());
			}
		}

		LOGGER.debug("saveFile() complete");

		return key;
	}

	private String getFullBucketName(String result)
	{
		return "s3://" + BUCKET_ROOT + BUCKET_PATH_SEPARATOR + environment + BUCKET_PATH_SEPARATOR + result;
	}

	public String retrieveFileByKey(String key)
	{
		String fileLocation = this.storage.get(key);

		if (ENV_LOCAL.equalsIgnoreCase(environment))
		{
			// TODO read from local
		} else
		{
			// TODO read from s3
		}

		return fileLocation;
	}
}
