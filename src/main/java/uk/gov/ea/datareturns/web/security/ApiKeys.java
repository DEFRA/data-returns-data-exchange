package uk.gov.ea.datareturns.web.security;

import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.gov.ea.datareturns.config.CryptoConfiguration;

/**
 * Created by graham on 13/05/16.
 */
@Service
public class ApiKeys {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeys.class);

	private final CryptoConfiguration config;

	@Inject
	public ApiKeys(final CryptoConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	public void showApiKeys() {
		if (this.config.isEnabled()) {
			LOGGER.info("Initializing ApiKeys service... ");
		} else {
			LOGGER.warn("ApiKeys service disabled via configuration.  This endpoint is not secured.");
		}
	}

	public boolean verifyAuthorizationHeader(final String givenAuthorizationHeader, final String dataToSign) {
		if (!this.config.isEnabled()) {
			return true;
		}
		final String calculatedAuthorizationHeader = calculateAuthorizationHeader(dataToSign);

		if (calculatedAuthorizationHeader == null || givenAuthorizationHeader == null) {
			return false;
		}
		return calculatedAuthorizationHeader.equals(givenAuthorizationHeader);
	}

	public String calculateAuthorizationHeader(final String dataToSign) {
		try {
			final LocalDate date = LocalDate.now();
			final String today = date.format(DateTimeFormatter.BASIC_ISO_DATE);
			final String dateKey = hmac(this.config.getSecretKey(), today);
			final String signingKey = hmac(dateKey, dataToSign);
			return signingKey;
		} catch (final SignatureException e) {
			LOGGER.info("Unable to calculate authorization header");
			return null;
		}
	}

	private String hmac(final String key, final String data) throws SignatureException {
		return hmac(data.getBytes(), key, this.config.getHashAlgorithm());
	}

//	private static String hmac(final String data, final String key, final String alg) throws SignatureException {
//		return hmac(data.getBytes(Charsets.UTF_8), key, alg);
//	}

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
