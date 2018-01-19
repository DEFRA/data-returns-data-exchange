package uk.gov.defra.datareturns.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Provides configuration support for defining service URI's
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@RequiredArgsConstructor
@Slf4j
@Data
public class ServiceEndpointConfiguration implements InitializingBean {
    /**
     * Master data API endpoint ID
     */
    public static final String MASTER_DATA_API = "master_data_api";
    private final ApplicationContext context;
    private Map<String, Endpoint> serviceEndpoints;


    /**
     * Retrieve the {@link Endpoint} configuration for a given identifier
     *
     * @param endpointId the identifier of the endpoint
     * @return the configuration for the endpoint, or null if not found
     */
    public Endpoint getEndpoint(final String endpointId) {
        return serviceEndpoints.get(endpointId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceEndpoints.forEach((api, endpoint) -> {
            if (!endpoint.getUri().getPath().endsWith("/")) {
                log.error(
                        "Endpoint URI path for {} should be terminated by a / but is set to: {}   Terminating.",
                        api, endpoint.getUri());
                SpringApplication.exit(context, () -> 1);
            }
        });
    }

    /**
     * Define the configurable authorization types
     */
    public enum AuthorizationType {
        /**
         * No auth
         */
        NONE((restTemplate, properties) -> {
        }),
        /**
         * HTTP Basic auth via the Authorization header
         */
        BASIC((restTemplate, properties) -> {
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    Objects.toString(properties.get("user")),
                    Objects.toString(properties.get("password"))
            ));
        });

        private final BiConsumer<RestTemplate, Map<String, Object>> configurer;

        /**
         * Enum constructor
         *
         * @param configurer consumer to configure the provided REST template and properties to add authorization support
         */
        AuthorizationType(final BiConsumer<RestTemplate, Map<String, Object>> configurer) {
            this.configurer = configurer;
        }
    }

    /**
     * Service endpoint configuration
     */
    @Data
    @ConfigurationProperties
    public static class Endpoint {
        /**
         * Endpoint URI
         */
        @NotNull
        private URI uri;
        /**
         * Endpoint Auth type
         */
        @NotNull
        private AuhthorizationConfiguration auth;
    }

    /**
     * Authorization configuration
     */
    @Data
    @ConfigurationProperties
    public static class AuhthorizationConfiguration {
        private AuthorizationType type;
        private Map<String, Object> properties;

        /**
         * Configure the provided REST template to add authorization support
         *
         * @param restTemplate the {@link RestTemplate} to be configured
         */
        public void configure(final RestTemplate restTemplate) {
            type.configurer.accept(restTemplate, properties);
        }
    }
}
