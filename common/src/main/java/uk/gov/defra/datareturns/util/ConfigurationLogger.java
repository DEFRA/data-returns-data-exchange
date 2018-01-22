package uk.gov.defra.datareturns.util;

import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides logging of configuration values on startup
 *
 * @author Sam Gardner-Dell
 */
@NoArgsConstructor
public class ConfigurationLogger implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        printConfiguration(environment);
    }

    /**
     * Print the configuration values to stdout - logging framework has not been initialised at this point.
     *
     * @param env the spring application environment.
     */

    private void printConfiguration(final ConfigurableEnvironment env) {
        final PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(env.getPropertySources());
        final Map<String, String> resolved = new TreeMap<>();

        env.getPropertySources().forEach(p -> {
            if (p instanceof EnumerablePropertySource) {
                final EnumerablePropertySource<?> source = (EnumerablePropertySource<?>) p;
                for (final String prop : source.getPropertyNames()) {
                    resolved.put(prop, resolver.getProperty(prop));
                }
            }
        });

        System.out.println("Found configuration properties:");
        resolved.forEach((k, v) -> System.out.println("    " + k + "=" + v));
    }
}
