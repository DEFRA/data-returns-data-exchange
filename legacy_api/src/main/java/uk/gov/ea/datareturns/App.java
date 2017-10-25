package uk.gov.ea.datareturns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

/**
 * Application class for the Data Returns Backend Service.
 *
 * @author Sam Gardner-Dell
 */
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class App {
    protected static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * Application main startup method
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }
}