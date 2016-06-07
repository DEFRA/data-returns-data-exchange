package uk.gov.ea.datareturns.web.security;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.config.CryptoConfiguration;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The API keys service used to secure the endpoint from unauthorised access.
 *
 * @author Graham
 */
@Service
public class ApiKeys {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeys.class);

	private final CryptoConfiguration config;

	/**
	 * Create a new instance of {@link ApiKeys}
	 *
	 * @param config the crypto configuration
	 */
	@Inject
	public ApiKeys(final CryptoConfiguration config) {
		this.config = config;
	}

	/**
	 * Post-construct hook to output the crypto configuration state to the console/logs
	 */
	@PostConstruct
	public void showApiKeys() {
		if (this.config.isEnabled()) {
			LOGGER.info("Initializing ApiKeys service... ");
		} else {
			LOGGER.warn("ApiKeys service disabled via configuration.  This endpoint is not secured.");
		}
	}

	/**
	 * Verify that the authorization header used on a request was correctly signed with the right key
	 *
	 * @param givenAuthorizationHeader the authorization header received on the a request
	 * @param dataToSign the server-calculated data that will be signed to check that it matches the given authorization header
	 * @return true if the authorization header is valid, false otherwise
	 */
	public boolean verifyAuthorizationHeader(final String givenAuthorizationHeader, final String dataToSign) {
		if (!this.config.isEnabled()) {
			return true;
		}
		final String calculatedAuthorizationHeader = calculateAuthorizationHeader(dataToSign);

		return calculatedAuthorizationHeader != null
				&& givenAuthorizationHeader != null
				&& calculatedAuthorizationHeader.equals(givenAuthorizationHeader);
	}

	/**
	 * Calculate the correct authorization header for a request
	 *
	 * @param dataToSign the request data to sign
	 * @return a one-way digest of the data to sign based on the configured key and algorithm
	 */
	public String calculateAuthorizationHeader(final String dataToSign) {
		try {
			final LocalDate date = LocalDate.now();
			final String today = date.format(DateTimeFormatter.BASIC_ISO_DATE);
			final String dateKey = hmac(today, this.config.getSecretKey());
			return hmac(dataToSign, dateKey);
		} catch (final SignatureException e) {
			LOGGER.info("Unable to calculate authorization header");
			return null;
		}
	}

	/**
	 * Use a one-way message digest MAC algorithm using a secret key
	 *
	 * @param data the data to be digested
	 * @param key the key used to create the digest
	 * @return the result of the digest algorithm
	 * @throws SignatureException if there is a problem with the key or if a MAC algorithm with the specified name does not exist.
	 */
	private String hmac(final String data, final String key) throws SignatureException {
		return hmac(data.getBytes(), key, this.config.getHashAlgorithm());
	}

	/**
	 * Use a one-way message digest MAC algorithm using a secret key
	 *
	 * @param data the data to be digested
	 * @param key the key used to create the digest
	 * @param alg the digest algorithm (e.g. SHA-256)
	 * @return the result of the digest algorithm
	 * @throws SignatureException if there is a problem with the key or if a MAC algorithm with the specified name does not exist.
	 */
	private static String hmac(final byte[] data, final String key, final String alg) throws SignatureException {
		try {
			final SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), alg);
			final Mac mac = Mac.getInstance(alg);
			mac.init(signingKey);
			return Hex.encodeHexString(mac.doFinal(data));
		} catch (KeyException | NoSuchAlgorithmException e) {
			throw new SignatureException(e);
		}
	}
}
