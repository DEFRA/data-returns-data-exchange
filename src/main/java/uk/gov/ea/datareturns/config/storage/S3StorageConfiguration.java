package uk.gov.ea.datareturns.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration;

/**
 * Settings for the Amazon S3 storage integration
 *
 * @author Sam Gardner-Dell
 */
@Component
@ConfigurationProperties(prefix="storage.s3")
public class S3StorageConfiguration implements AmazonS3Configuration {
	@JsonProperty("awsAccessKey")
	private String awsAccessKey;

	@JsonProperty("awsSecretKey")
	private String awsSecretKey;

	@JsonProperty("temporaryBucket")
	private String temporaryBucket;

	@JsonProperty("persistentBucket")
	private String persistentBucket;

	@JsonProperty("endpoint")
	private String endpoint;

	@JsonProperty("protocol")
	private String protocol;

	@JsonProperty("proxyHost")
	private String proxyHost;

	@JsonProperty("proxyPort")
	private int proxyPort;

	@JsonProperty("pathStyleAccess")
	private boolean pathStyleAccess = false;

	public S3StorageConfiguration() {
	}

	/**
	 * @return the awsAccessKey
	 */
	public String getAwsAccessKey() {
		return this.awsAccessKey;
	}

	/**
	 * @param awsAccessKey the awsAccessKey to set
	 */
	public void setAwsAccessKey(final String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	/**
	 * @return the awsSecretKey
	 */
	public String getAwsSecretKey() {
		return this.awsSecretKey;
	}

	/**
	 * @param awsSecretKey the awsSecretKey to set
	 */
	public void setAwsSecretKey(final String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration#getTemporaryBucket()
	 */
	@Override
	public String getTemporaryBucket() {
		return this.temporaryBucket;
	}

	/**
	 * @param temporaryBucket the temporaryBucket to set
	 */
	public void setTemporaryBucket(final String temporaryBucket) {
		this.temporaryBucket = temporaryBucket;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration#getPersistentBucket()
	 */
	@Override
	public String getPersistentBucket() {
		return this.persistentBucket;
	}

	/**
	 * @param persistentBucket the persistentBucket to set
	 */
	public void setPersistentBucket(final String persistentBucket) {
		this.persistentBucket = persistentBucket;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration#getEndpoint()
	 */
	@Override
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(final String endpoint) {
		this.endpoint = StringUtils.isNotBlank(endpoint) ? endpoint : null;
	}

	/**
	 * @return
	 */
	public String getProtocol() {
		return this.protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(final String protocol) {
		this.protocol = StringUtils.isNotBlank(protocol) ? protocol : null;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return this.proxyHost;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(final String proxyHost) {
		this.proxyHost = StringUtils.isNotBlank(proxyHost) ? proxyHost : null;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return this.proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(final int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the pathStyleAccess
	 */
	@Override
	public boolean isPathStyleAccess() {
		return this.pathStyleAccess;
	}

	/**
	 * @param pathStyleAccess the pathStyleAccess to set
	 */
	public void setPathStyleAccess(final boolean pathStyleAccess) {
		this.pathStyleAccess = pathStyleAccess;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration#getCredentialProvider()
	 */
	@Override
	public AWSCredentialsProvider getCredentialProvider() {
		final AWSCredentials credentials = new BasicAWSCredentials(this.awsAccessKey, this.awsSecretKey);
		return new StaticCredentialsProvider(credentials);
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Configuration#getClientConfiguration()
	 */
	@Override
	public ClientConfiguration getClientConfiguration() {
		final ClientConfiguration s3Config = new ClientConfiguration();
		if (this.protocol != null) {
			s3Config.setProtocol("https".equalsIgnoreCase(this.protocol) ? Protocol.HTTPS : Protocol.HTTP);
		}
		if (this.proxyHost != null) {
			s3Config.setProxyHost(this.proxyHost);
		}
		s3Config.setProxyPort(this.proxyPort);
		return s3Config;
	}
}