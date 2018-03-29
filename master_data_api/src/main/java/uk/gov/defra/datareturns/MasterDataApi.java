package uk.gov.defra.datareturns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.gov.defra.datareturns.config.DataLoaderConfiguration;

/**
 * Application class for the Data Returns Backend Service.
 *
 * @author Sam Gardner-Dell
 */
@SpringBootApplication
@Slf4j
@SuppressWarnings({"NonFinalUtilityClass", "HideUtilityClassConstructor"})
public class MasterDataApi {
    /**
     * Application main startup method
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(MasterDataApi.class, args);

        final DataLoaderConfiguration dataLoaderConfiguration = context.getBean(DataLoaderConfiguration.class);
        if (dataLoaderConfiguration.isRunAtStartup() && dataLoaderConfiguration.isShutdownAfterLoad()) {
            log.info("Data load complete, shutting down");
            System.exit(SpringApplication.exit(context));
        }
    }
}
