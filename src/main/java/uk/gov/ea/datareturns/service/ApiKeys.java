package uk.gov.ea.datareturns.service;

import org.apache.commons.codec.Charsets;
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

import static org.apache.commons.codec.binary.Hex.encodeHexString;

/**
 * Created by graham on 13/05/16.
 */
@Service
public class ApiKeys {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeys.class);

    @Inject
    private CryptoConfiguration config;

    @PostConstruct
    public void showApiKeys() {
        LOGGER.info("Initializing ApiKeys service... ");
    }

    public boolean verifyAuthorizationHeader(String givenAuthorizationHeader, String dataToSign) {
        String calculatedAuthorizationHeader = calculateAuthorizationHeader(dataToSign);

        if (calculatedAuthorizationHeader == null || givenAuthorizationHeader == null) {
            return false;
        } else {
            return calculatedAuthorizationHeader.equals(givenAuthorizationHeader);
        }
    }

    public String calculateAuthorizationHeader(String dataToSign) {
        try {
            LocalDate date = LocalDate.now();
            String today = date.format(DateTimeFormatter.BASIC_ISO_DATE);
            String dateKey = hmac(config.getSecretKey(), today);
            String signingKey = hmac(dateKey, dataToSign);
            return signingKey;
        } catch (SignatureException e) {
            LOGGER.info("Unable to calculate authorization header");
            return null;
        }
    }

    private String hmac(String key, String data) throws SignatureException {
        return hmac(data.getBytes(), key, config.getHashAlgorithm());
    }

    private String hmac(String data, String key, String alg) throws SignatureException {
        return hmac(data.getBytes(Charsets.UTF_8), key, alg);
    }

    private String hmac(byte[] data, String key, String alg) throws SignatureException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), alg);
            Mac mac = Mac.getInstance(alg);
            mac.init(signingKey);
            return Hex.encodeHexString(mac.doFinal(data));
        } catch (KeyException | NoSuchAlgorithmException e) {
            throw new SignatureException(e);
        }
    }
}
