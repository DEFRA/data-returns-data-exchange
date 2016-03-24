
package uk.gov.ea.datareturns.storage;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DnsResolver;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;

import uk.gov.ea.datareturns.storage.StorageProvider.StoredFile;
import uk.gov.ea.datareturns.storage.s3.AmazonS3Settings;
import uk.gov.ea.datareturns.storage.s3.AmazonS3StorageProvider;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmazonS3IntegrationTests {
	/*
	 * TODO:  These tests are not ready for use and depend on us finding a suitable S3 emulator
	 * Have tried s3ninja and fakes3 and neither are behaving the same as when I run these tests
	 * against S3 using my test account.
	 * They could use a fair bit of tidying up too!
	 */
	public static final String HEADER_ORIGINATOR_EMAIL = "originator-email";
	public static final String HEADER_ORIGINAL_FILENAME = "original-filename";
	private static final AmazonS3Settings SETTINGS = new AmazonS3Settings() {
		@Override
		public boolean isPathStyleAccess() { return false; }
		@Override
		public String getTemporaryBucket() { return "data-returns-test-temp"; }
		@Override
		public String getPersistentBucket() { return "data-returns-test"; }
		@Override
		public String getEndpoint() {
			// Fakes3 testing
//			return "http://localhost:10453";
			// S3ninja testing
//			return "http://localhost:9444/s3";
			return null;
		}
		@Override
		public AWSCredentialsProvider getCredentialProvider() {
			// s3Ninja credentials
			String accessKey = "AKIAIOSFODNN7EXAMPLE";
			String secretKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
			return new StaticCredentialsProvider(credentials); 
		}
		@Override
		public ClientConfiguration getClientConfiguration() {
			final ClientConfiguration s3Config = new ClientConfiguration();
			
			// Force S3 connections to localhost (saves adding host file entries for default vhost style S3 access)
			s3Config.setDnsResolver(new DnsResolver() {
				@Override
				public InetAddress[] resolve(String host) throws UnknownHostException {
					return new InetAddress[] {
						InetAddress.getLoopbackAddress()
					};
				}
			});
			return s3Config;
		}
	};

	private static String fileKey;
	private static AmazonS3StorageProvider storageProvider;
	
	
	@BeforeClass
	public static void setUp() {
		storageProvider = new AmazonS3StorageProvider(SETTINGS);
	}
	
	@Test
	public void test1PutRequest() {
		try {
			File file = new File(AmazonS3IntegrationTests.class.getResource("/testfiles/required-fields-only.csv").toURI());
			Map<String, String> metadata = new TreeMap<>();
			metadata.put(HEADER_ORIGINAL_FILENAME, file.getName());
			metadata.put(HEADER_ORIGINATOR_EMAIL, "me@here.now");
			fileKey = storageProvider.store(SETTINGS.getTemporaryBucket(), file, metadata);
			Assert.assertTrue(fileKey != null && !fileKey.isEmpty());
			System.out.println("test1PutRequest: received API key " + fileKey);
		} catch (URISyntaxException | StorageException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void test2UpdateRequest() {
		try {
			Map<String, String> metadata = new TreeMap<>();
			metadata.put(HEADER_ORIGINAL_FILENAME, fileKey);
			metadata.put(HEADER_ORIGINATOR_EMAIL, "me@here.now");
			storageProvider.updateMetadata(SETTINGS.getTemporaryBucket(), fileKey, metadata);
		} catch (StorageException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void test3GetRequest() {
		try {
			StoredFile storedFile = storageProvider.retrieve(SETTINGS.getTemporaryBucket(), fileKey);
			Assert.assertTrue("Unable to retrieve object from S3", storedFile != null);
			Assert.assertEquals("you@there.then", storedFile.getMetadata().get(HEADER_ORIGINATOR_EMAIL));
		} catch (StorageException e) {
			Assert.fail(e.getMessage());
		}
	}			
	
	@Test
	public void test4InvalidGetRequest() {
		
		try {
			StoredFile storedFile = storageProvider.retrieve(SETTINGS.getTemporaryBucket(), "A FILEKEY THAT DOES NOT EXIST");
			Assert.assertTrue("Stored file should be null when trying to access a key that does not exist.", storedFile == null);
		} catch (StorageException e) {
			Assert.fail(e.getMessage());
		}
	}		
}