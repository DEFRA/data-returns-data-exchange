package uk.gov.defra.datareturns.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Provides configuration support for the data loader (to load base data)
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "dataloader")
@RequiredArgsConstructor
@Slf4j
@Data
public class DataLoaderConfiguration {
    /**
     * should the data loader be run on API startup
     */
    private boolean runAtStartup;
    /**
     * should the service be shutdown after completion of the data loader if run at startup
     */
    private boolean shutdownAfterLoad;
}
