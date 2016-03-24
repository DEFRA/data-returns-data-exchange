package uk.gov.ea.datareturns.config.storage;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.ea.datareturns.storage.s3.AmazonS3Settings;

/**
 * Settings for the Amazon S3 storage integration
 * 
 * @author Sam Gardner-Dell
 */
public class S3StorageSettings implements AmazonS3Settings {
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
	
	public S3StorageSettings() {
	}

	
	/**
	 * @return the awsAccessKey
	 */
	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	/**
	 * @param awsAccessKey the awsAccessKey to set
	 */
	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	/**
	 * @return the awsSecretKey
	 */
	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	/**
	 * @param awsSecretKey the awsSecretKey to set
	 */
	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Settings#getTemporaryBucket()
	 */
	@Override
	public String getTemporaryBucket() {
		return temporaryBucket;
	}

	/**
	 * @param temporaryBucket the temporaryBucket to set
	 */
	public void setTemporaryBucket(String temporaryBucket) {
		this.temporaryBucket = temporaryBucket;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Settings#getPersistentBucket()
	 */
	@Override
	public String getPersistentBucket() {
		return persistentBucket;
	}

	/**
	 * @param persistentBucket the persistentBucket to set
	 */
	public void setPersistentBucket(String persistentBucket) {
		this.persistentBucket = persistentBucket;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Settings#getEndpoint()
	 */
	@Override
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = StringUtils.isNotBlank(endpoint) ? endpoint : null;
	}

	/**
	 * @return
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = StringUtils.isNotBlank(protocol) ? protocol : null;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = StringUtils.isNotBlank(proxyHost) ? proxyHost : null;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the pathStyleAccess
	 */
	public boolean isPathStyleAccess() {
		return pathStyleAccess;
	}

	/**
	 * @param pathStyleAccess the pathStyleAccess to set
	 */
	public void setPathStyleAccess(boolean pathStyleAccess) {
		this.pathStyleAccess = pathStyleAccess;
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Settings#getCredentialProvider()
	 */
	@Override
	public AWSCredentialsProvider getCredentialProvider() {
		final AWSCredentials credentials = new BasicAWSCredentials(this.awsAccessKey, this.awsSecretKey);
		return new StaticCredentialsProvider(credentials); 
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.storage.s3.AmazonS3Settings#getClientConfiguration()
	 */
	@Override
	public ClientConfiguration getClientConfiguration() {
		final ClientConfiguration s3Config = new ClientConfiguration();
		if (protocol != null) {
			s3Config.setProtocol("http".equalsIgnoreCase(protocol) ? Protocol.HTTP : Protocol.HTTPS);
		}
		if (proxyHost != null) {
			s3Config.setProxyHost(proxyHost);
		}
		s3Config.setProxyPort(proxyPort);
		return s3Config;
	}
}