package uk.gov.ea.datareturns.web.config;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.web.filters.AuthorizationFilterFileUpload;
import uk.gov.ea.datareturns.web.resource.ControlledListResource;
import uk.gov.ea.datareturns.web.resource.DataExchangeResource;
import uk.gov.ea.datareturns.web.resource.LoggingTestResource;

import javax.inject.Inject;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Set;

/**
 * Jersey configuration class
 *
 * @author Sam Gardner-Dell
 */
@Component
public class JerseyConfig extends ResourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfig.class);

    /**
     * Configuration for the Jersey 2 RESTful services
     *
     * @param context the spring application context
     */
    @Inject
    public JerseyConfig(final ApplicationContext context) {
        super();
        super.setApplicationName("Data Returns Backend REST Service");

        // Use spring scanner to discover ExceptionMapper classes
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ExceptionMapper.class));
        final Set<BeanDefinition> result = scanner.findCandidateComponents("uk.gov.ea.datareturns.web.exceptionmappers");

        for (final BeanDefinition defintion : result) {
            try {
                LOGGER.info("Registering JAX-RS Exception Mapper: " + defintion.getBeanClassName());
                register(Class.forName(defintion.getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                LOGGER.error("Failed to register exception mapper.", e);
            }
        }

        // Register resources
        register(context.getBean(DataExchangeResource.class));
        register(context.getBean(ControlledListResource.class));
        register(context.getBean(LoggingTestResource.class));

        // Register features
        register(new MultiPartFeature());
        register(context.getBean(AuthorizationFilterFileUpload.class));

        // Configure the logging filter based on log configuration
        final Logger loggingFilterLogger = LoggerFactory.getLogger(LoggingFilter.class);
        final boolean attachLogger = loggingFilterLogger.isInfoEnabled();
        final boolean printEntity = loggingFilterLogger.isDebugEnabled();
        if (attachLogger || printEntity) {
            final java.util.logging.Logger jerseyJulLogger = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());
            register(new LoggingFilter(jerseyJulLogger, printEntity));
        }
    }
}
