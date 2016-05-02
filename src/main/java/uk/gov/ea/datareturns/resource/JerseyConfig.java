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
	public JerseyConfig(ApplicationContext context) {
		register(context.getBean(DataExchangeResource.class));
		
		register(new MultiPartFeature());
		
		// Configure the logging filter based on log configuration
		Logger loggingFilterLogger = LoggerFactory.getLogger(LoggingFilter.class);
		boolean attachLogger = loggingFilterLogger.isInfoEnabled();
		boolean printEntity = loggingFilterLogger.isDebugEnabled();
		if (attachLogger || printEntity) {
			java.util.logging.Logger jerseyJulLogger = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());
			register(new LoggingFilter(jerseyJulLogger, printEntity));
		}
	}
}
