package uk.gov.ea.datareturns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import uk.gov.ea.datareturns.domain.jpa.loader.DatabaseLoader;

import java.util.Map;

/**
 * Executable class to load the spring context and execute all {@link DatabaseLoader} instances found within the context
 *
 * @author Sam Gardner-Dell
 */
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
@EnableAutoConfiguration(exclude = { JerseyAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class,
        WebMvcAutoConfiguration.class, WebSocketAutoConfiguration.class, EndpointAutoConfiguration.class })
public class DataLoader {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    /**
     * Load the spring context and execute all {@link DatabaseLoader} instances found within the context
     *
     * @param args
     */
    public static void main(final String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(DataLoader.class, args)) {
            Map<String, DatabaseLoader> loaderBeans = ctx.getBeansOfType(DatabaseLoader.class);

            loaderBeans.forEach((name, loader) -> {
                LOGGER.info("Executing base data loader: " + name);
                loader.load();
            });
        }
        System.exit(0);
    }
}