/**
 * 
 */
package uk.gov.ea.datareturns.unittests;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.storage.S3StorageConfiguration;

/**
 * Tests {@link S3StorageConfiguration} output
 * 
 * @author Sam Gardner-Dell
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("S3StorageConfigurationTests")
@DirtiesContext
public class S3StorageConfigurationTests {
	// Note: These constants should match the values in S3ConfigTest.yml
	private static final String AWS_ACCESS_KEY = "AWS_ACCESS_KEY";
	private static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
	private static final String AWS_PERSISTENT_BUCKET = "AWS_PERSISTENT_BUCKET";
	private static final String AWS_TEMPORARY_BUCKET = "AWS_TEMPORARY_BUCKET";
	private static final String AWS_ENDPOINT = "AWSENDPOINT";
	private static final String AWS_PROTOCOL = "http";
	private static final String AWS_PROXY_HOST = "AWS_PROXY_HOST";
	private static final int AWS_PROXY_PORT = 1234;
	private static final boolean AWS_PATHSTYLE_ACCESS = false;

	@Inject
	private S3StorageConfiguration config;

	@Test
	public void testConfiguration() {
		Assert.assertEquals(config.getAwsAccessKey(), AWS_ACCESS_KEY);
		Assert.assertEquals(config.getAwsSecretKey(), AWS_SECRET_KEY);
		Assert.assertEquals(config.getPersistentBucket(), AWS_PERSISTENT_BUCKET);
		Assert.assertEquals(config.getTemporaryBucket(), AWS_TEMPORARY_BUCKET);
		Assert.assertEquals(config.getEndpoint(), AWS_ENDPOINT);
		Assert.assertEquals(config.getProtocol(), AWS_PROTOCOL);
		Assert.assertEquals(config.getProxyHost(), AWS_PROXY_HOST);
		Assert.assertEquals(config.getProxyPort(), AWS_PROXY_PORT);
		Assert.assertEquals(config.isPathStyleAccess(), AWS_PATHSTYLE_ACCESS);
	}


	@Test
	public void testClientConfiguration() {
		ClientConfiguration cfg = config.getClientConfiguration();
		Assert.assertEquals(cfg.getProxyHost(), AWS_PROXY_HOST);
		Assert.assertEquals(cfg.getProxyPort(), AWS_PROXY_PORT);
		Assert.assertEquals(cfg.getProtocol(), Protocol.HTTP);
	}

	@Test
	public void testCredentials() {
		AWSCredentials creds = config.getCredentialProvider().getCredentials();
		Assert.assertEquals(creds.getAWSAccessKeyId(), AWS_ACCESS_KEY);
		Assert.assertEquals(creds.getAWSSecretKey(), AWS_SECRET_KEY);
	}
}