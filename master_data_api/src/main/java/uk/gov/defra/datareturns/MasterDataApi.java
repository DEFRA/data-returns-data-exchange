package uk.gov.defra.datareturns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.gov.defra.datareturns.config.DataLoaderConfiguration;
import uk.gov.defra.datareturns.data.loader.DataLoader;

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
        if (dataLoaderConfiguration.isRunAtStartup()) {
            loadBaselineData(context);
            if (dataLoaderConfiguration.isShutdownAfterLoad()) {
                System.exit(SpringApplication.exit(context));
            }
        }
    }

    /**
     * Load baseline CSV data into the database
     *
     * @param context the application context
     */
    private static void loadBaselineData(final ConfigurableApplicationContext context) {
        final DataLoader loader = context.getBean(DataLoader.class);
        try {
            loader.loadAll();
        } catch (final Throwable t) {
            log.error("Failed to load master data.", t);
            System.exit(-1);
        }
    }
}
