package uk.gov.ea.datareturns.config;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by graham on 13/05/16.
 */
@Configuration
@ConfigurationProperties(prefix = "crypt")
public class CryptoConfiguration {
	@NotNull
	private String secretKey;

	@NotNull
	private String hashAlgorithm;

	private boolean enabled = true;

	public String getSecretKey() {
		return this.secretKey;
	}

	public void setSecretKey(final String secretKey) {
		this.secretKey = secretKey;
	}

	public String getHashAlgorithm() {
		return this.hashAlgorithm;
	}

	public void setHashAlgorithm(final String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}
}
