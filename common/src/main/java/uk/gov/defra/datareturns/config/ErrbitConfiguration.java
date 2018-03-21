package uk.gov.defra.datareturns.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * Errbit (airbrake) integration configuration
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "errbit")
@RequiredArgsConstructor
@Slf4j
@Data
public class ErrbitConfiguration {
    private boolean enabled;
    private String env;
    private String apiKey;
    private URL url;

}
