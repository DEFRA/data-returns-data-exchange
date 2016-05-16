package uk.gov.ea.datareturns.resource;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ext.ExceptionMapper;

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

@Component
public class JerseyConfig extends ResourceConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfig.class);

	@Inject
	public JerseyConfig(final ApplicationContext context) {
		super();
		super.setApplicationName("Data Returns Backend REST Service");

		// Use spring scanner to discover ExceptionMapper classes
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(ExceptionMapper.class));
		final Set<BeanDefinition> result = scanner.findCandidateComponents("uk.gov.ea.datareturns.resource");

		for (final BeanDefinition defintion : result) {
			try {
				LOGGER.info("Registering JAX-RS Exception Mapper: " + defintion.getBeanClassName());
				register(Class.forName(defintion.getBeanClassName()));
			} catch (ClassNotFoundException e) {
				LOGGER.error("Failed to register exception mapper.", e);
			}
		}

		// Register resources
		register(context.getBean(DataExchangeResource.class));

		// Register features
		register(new MultiPartFeature());
		register(new AuthorizationFilterFileUpload());

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
