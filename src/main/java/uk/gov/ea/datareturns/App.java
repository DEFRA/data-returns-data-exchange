package uk.gov.ea.datareturns;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import uk.gov.ea.datareturns.resources.DataExchangeConfiguration;
import uk.gov.ea.datareturns.resources.UploadResource;

public class App extends Application<DataExchangeConfiguration>
{
	public static void main(String[] args) throws Exception
	{
		new App().run(args);
	}

	@Override
	public void initialize(Bootstrap<DataExchangeConfiguration> bootstrap)
	{
	}

	@Override
	public void run(DataExchangeConfiguration configuration, Environment environment)
	{
		configureCors(environment);
		environment.jersey().register(new UploadResource(configuration));
	}

	private void configureCors(Environment environment)
	{
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}
}
