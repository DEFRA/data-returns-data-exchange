package uk.gov.defra.datareturns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application class for the Data Returns ECM submissions API.
 *
 * @author Sam Gardner-Dell
 */
@SpringBootApplication
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "NonFinalUtilityClass"})
public class EcmApi {
    /**
     * Application main startup method
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(EcmApi.class, args);
    }
}
