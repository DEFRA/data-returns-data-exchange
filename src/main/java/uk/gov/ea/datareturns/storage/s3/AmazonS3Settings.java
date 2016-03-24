/**
 * 
 */
package uk.gov.ea.datareturns.storage.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * Interface used to supply Amazon S3 configuration details to the {@link AmazonS3StorageProvider}
 * 
 * @author Sam Gardner-Dell
 */
public interface AmazonS3Settings {
	/**
	 * Provides authentication credentials for Amazon S3
	 * @return an {@link AWSCredentialsProvider} instance
	 */
	AWSCredentialsProvider getCredentialProvider();
	
	/**
	 * S3 Client configuration settings.  Allows things like the proxy and protocol to be set
	 * 
	 * @return a {@link ClientConfiguration} instance
	 */
	ClientConfiguration getClientConfiguration();
	
	/**
	 * Configure the Amazon S3 Endpoint URL.  If this returns null then the default Amazon service endpoint
	 * will be used.  This is useful to set the endpoint to a particular region or for testing using a
	 * local S3 emulator.
	 */
	String getEndpoint();
	
	/**
	 * The name of the S3 bucket used for temporary storage of user data.  This is used until the user
	 * submits their data at the final step (at which point the data is moved from this bucket to the
	 * audit bucket)
	 * @return the bucket name of the temporary storage area as a {@link String}
	 */
	String getTemporaryBucket();
	
	/**
	 * The name of the S3 bucket used for "permanent" storage of user data for audit purposes.
	 * File stored in this bucket will have also been submitted to EMMA but are kept in this bucket
	 * for an agreed period (90 days) for audit/legal purposes.
	 * @return the bucket name of the permanent storage area as a {@link String}
	 */
	String getPersistentBucket();
	
	/**
	 * Controls how the Amazon S3 client communicates with Amazon S3
	 * 
	 * If false (the default), requests will use URLs such as http://bucketname.s3.amazonaws.com
	 * If true, requests will use URLS such as http://s3.amazonaws.com/bucketname/
	 * 
	 * This can be useful for local testing (where the DNS lookup for bucketname.host would fail without
	 * additional host file entries or DNS aliases)
	 * 
	 * @return true if path style access should be used, false otherwise.
	 */
	boolean isPathStyleAccess();
}
