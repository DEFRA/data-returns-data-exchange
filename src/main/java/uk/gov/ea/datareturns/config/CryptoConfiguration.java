package uk.gov.ea.datareturns.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created by graham on 13/05/16.
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "crypt")
public class CryptoConfiguration {

    @NotNull
    private String secretKey;

    @NotNull
    private String hashAlgorithm;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
}
