package uk.gov.ea.datareturns.config;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by graham on 13/05/16.
 *
 * @author Graham
 */
@Configuration
@ConfigurationProperties(prefix = "crypt")
public class CryptoConfiguration {
	@NotNull
	private String secretKey;

	@NotNull
	private String hashAlgorithm;

	private boolean enabled = true;

	/**
	 * @return the secret key configured for this endpoint
	 */
	public String getSecretKey() {
		return this.secretKey;
	}

	/**
	 * Set the secret key configured for this endpoint
	 *
	 * @param secretKey the secret key configured for this endpoint
	 */
	public void setSecretKey(final String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return the HMAC algorithm to use for signing (e.g. SHA-256)
	 */
	public String getHashAlgorithm() {
		return this.hashAlgorithm;
	}

	/**
	 * Set the HMAC algorithm to use for signing (e.g. SHA-256)
	 *
	 * @param hashAlgorithm the HMAC algorithm to use for signing (e.g. SHA-256)
	 */
	public void setHashAlgorithm(final String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	/**
	 * Is this endpoint secured by API Key?
	 *
	 * @return true if the endpoint is secured, false otherwise
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Enable or disable API key security
	 *
	 * @param enabled to enable or disable API key security
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}
}
