package uk.gov.defra.datareturns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.gov.defra.datareturns.data.loader.DatabaseLoader;

import java.util.Map;

/**
 * Application class for the Data Returns Backend Service.
 *
 * @author Sam Gardner-Dell
 */
@SpringBootApplication
@Slf4j
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class MasterDataApi {
    /**
     * Application main startup method
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(MasterDataApi.class, args);

        if (context.getEnvironment().acceptsProfiles("dataloader")) {
            final Map<String, DatabaseLoader> loaderBeans = context.getBeansOfType(DatabaseLoader.class);
            loaderBeans.forEach((name, loader) -> {
                try {
                    log.info("Executing base data loader: {}", name);
                    loader.load();
                } catch (final Throwable t) {
                    log.error("Exception thrown by data loader " + name, t);
                    System.exit(-1);
                }
            });

            final int exitCode = SpringApplication.exit(context);
            if (exitCode != 0) {
                log.warn("Data loader exitted with non-zero exit code: {}", exitCode);
            }
            System.exit(exitCode);
        }
    }
}
