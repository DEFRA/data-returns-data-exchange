package uk.gov.ea.datareturns;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.resource.SubmitReturnsResource;

public class TestApplication extends Application<DataExchangeConfiguration>
{
	@Override
	public void run(DataExchangeConfiguration config, Environment environment) throws Exception
	{
		environment.jersey().register(new SubmitReturnsResource(config));
		environment.jersey().register(new MultiPartFeature());
	}
}
