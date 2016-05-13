package uk.gov.ea.datareturns.resource;

import javax.inject.Inject;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
	@Inject
	public JerseyConfig(final ApplicationContext context) {
		packages("uk.gov.ea.datareturns.resource");

		register(context.getBean(DataExchangeResource.class));

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
