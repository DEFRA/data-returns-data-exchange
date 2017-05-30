package uk.gov.ea.datareturns.web.config;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import io.swagger.annotations.Api;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.web.filters.AuthorizationFilterFileUpload;
import uk.gov.ea.datareturns.web.resource.ControlledListResource;
import uk.gov.ea.datareturns.web.resource.DataExchangeResource;
import uk.gov.ea.datareturns.web.resource.LoggingTestResource;
import uk.gov.ea.datareturns.web.resource.PermitLookupResource;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Jersey configuration class
 *
 * @author Sam Gardner-Dell
 */
@Component
@ApplicationPath(JerseyConfig.APPLICATION_PATH)
public class JerseyConfig extends ResourceConfig {
    public static final String APPLICATION_PATH = "/api/v1";

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfig.class);

    private final ApplicationContext context;

    /**
     * Configuration for the Jersey 2 RESTful services
     *
     * @param context the spring application context
     */
    @Inject
    public JerseyConfig(final ApplicationContext context) {
        super();
        super.setApplicationName("Data Returns API");
        this.context = context;
        registerExceptionMappers();
        registerEndpoints();
        registerFeatures();
        configureSwagger();
    }

    private void registerEndpoints() {
        // Register resources
        LOGGER.info("Registering JAX-RS Resources");
        register(context.getBean(DataExchangeResource.class));
        register(context.getBean(ControlledListResource.class));
        register(context.getBean(LoggingTestResource.class));
        register(context.getBean(PermitLookupResource.class));

        // Use spring scanner to discover API resources
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Api.class));
        registerBeans(scanner.findCandidateComponents("uk.gov.ea.datareturns.web.resource.v1"));

        // Access through /<Jersey's @ApplicationPath>/application.wadl (/api/v1/application.wadl)
        this.register(WadlResource.class);
    }

    private void registerExceptionMappers() {
        // Use spring scanner to discover ExceptionMapper classes
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ExceptionMapper.class));
        registerBeans(scanner.findCandidateComponents("uk.gov.ea.datareturns.web.exceptionmappers"));
    }

    private void registerBeans(Set<BeanDefinition> definitions) {
        for (final BeanDefinition definition : definitions) {
            try {
                LOGGER.info("Registering " + definition.getBeanClassName() + " with JAX-RS application");
                register(Class.forName(definition.getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                LOGGER.error("Failed to register bean " + definition.getBeanClassName(), e);
            }
        }
    }

    private void registerFeatures() {
        // Register features
        LOGGER.info("Registering JAX-RS Features");
        register(JacksonFeature.class);
        register(JacksonJaxbXMLProvider.class);
        register(new MultiPartFeature());
        register(context.getBean(AuthorizationFilterFileUpload.class));

        // Configure the logging filter based on log configuration
        final Logger loggingFilterLogger = LoggerFactory.getLogger(LoggingFeature.class);
        final boolean attachLogger = loggingFilterLogger.isInfoEnabled();
        final LoggingFeature.Verbosity verbosity = loggingFilterLogger.isDebugEnabled() ? LoggingFeature.Verbosity.PAYLOAD_ANY :
                LoggingFeature.Verbosity.HEADERS_ONLY;
        if (attachLogger) {
            final java.util.logging.Logger jerseyJulLogger = java.util.logging.Logger.getLogger(LoggingFeature.class.getName());
            register(new LoggingFeature(jerseyJulLogger, verbosity));
        }
    }

    private void configureSwagger() {
        LOGGER.info("Configuring swagger");
        // Access through /<Jersey's @ApplicationPath>/swagger.json (/api/v1/swagger.json)
        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        try {
            final String desc = IOUtils.toString(JerseyConfig.class.getResourceAsStream("/api_desc.md"), StandardCharsets.UTF_8);
            config.setDescription(desc);
        } catch (IOException e) {
            LOGGER.error("Unable to read api_desc.md", e);
        }

        config.setLicense("OGL v3.0");
        config.setLicenseUrl("http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/");
        config.setTitle("Data Returns API");
        String version = "UNKNOWN";
        try {
            version = PropertiesLoaderUtils.loadAllProperties("version.properties").getProperty("version");
        } catch (IOException e) {
            LOGGER.error("Unable to read version.properties");
        }

        config.setVersion(version);
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(APPLICATION_PATH);
        config.setResourcePackage("uk.gov.ea.datareturns.web.resource.v1");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}