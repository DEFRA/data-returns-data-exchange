package uk.gov.ea.datareturns.config;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.util.Environment;
import uk.gov.ea.datareturns.web.resource.JerseyResource;

import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Jersey REST services configuration
 *
 * @author Sam Gardner-Dell
 */
@Configuration
public class JerseyConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfiguration.class);

    @Value("${spring.jersey.application-path}")
    private String applicationPath;

    public String getApplicationPath() {
        return applicationPath;
    }

    @Bean ResourceConfig resourceConfig() {
        ResourceConfig config = new ResourceConfig();
        config.setApplicationName("Data Returns API");
        return config;
    }

    @Bean ResourceConfigCustomizer featureConfiguration() {
        return config -> {
            // Register features
            LOGGER.info("Registering JAX-RS Features");
            config.register(JacksonFeature.class);
            config.register(JacksonJaxbXMLProvider.class);

            // Configure the logging filter based on log configuration
            final Logger loggingFilterLogger = LoggerFactory.getLogger(LoggingFeature.class);
            final boolean attachLogger = loggingFilterLogger.isInfoEnabled();
            final LoggingFeature.Verbosity verbosity = loggingFilterLogger.isDebugEnabled() ? LoggingFeature.Verbosity.PAYLOAD_ANY :
                    LoggingFeature.Verbosity.HEADERS_ONLY;
            if (attachLogger) {
                final java.util.logging.Logger jerseyJulLogger = java.util.logging.Logger.getLogger(LoggingFeature.class.getName());
                config.register(new LoggingFeature(jerseyJulLogger, verbosity));
            }
        };
    }

    @Bean ResourceConfigCustomizer resourceConfiguration(Map<String, ? extends JerseyResource> resources) {
        return config -> {
            LOGGER.info("Registering JAX-RS Resources");
            resources.values().forEach(config::register);

            // Access through /<Jersey's @ApplicationPath>/application.wadl (/api/v1/application.wadl)
            config.register(WadlResource.class);
        };
    }

    @Bean ResourceConfigCustomizer exceptionConfiguration(Map<String, ? extends ExceptionMapper> mappers) {
        return config -> {
            LOGGER.info("Registering JAX-RS Exception Mappers");
            mappers.values().forEach(config::register);
        };
    }

    @Bean ResourceConfigCustomizer swaggerConfiguration() {
        return config -> {
            LOGGER.info("Configuring swagger");
            // Access through /<Jersey's @ApplicationPath>/swagger.json (/api/v1/swagger.json)
            config.register(ApiListingResource.class);
            config.register(SwaggerSerializers.class);

            BeanConfig swaggerBean = new BeanConfig();
            try {
                final String desc = IOUtils.toString(JerseyConfiguration.class.getResourceAsStream("/api_desc.md"), StandardCharsets.UTF_8);
                swaggerBean.setDescription(desc);
            } catch (IOException e) {
                LOGGER.error("Unable to read api_desc.md", e);
            }

            swaggerBean.setLicense("OGL v3.0");
            swaggerBean.setLicenseUrl("http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/");
            swaggerBean.setTitle("Data Returns API");
            swaggerBean.setVersion(Environment.getVersion());
            swaggerBean.setSchemes(new String[] { "http", "https" });
            swaggerBean.setBasePath(applicationPath);
            swaggerBean.setResourcePackage("uk.gov.ea.datareturns.web.resource.v1");
            swaggerBean.setPrettyPrint(true);
            swaggerBean.setScan(true);
        };
    }
}
