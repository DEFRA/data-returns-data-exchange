package uk.gov.ea.datareturns.health;

import com.codahale.metrics.health.HealthCheck;

public class DataReturnsHealthCheck extends HealthCheck
{

	@Override
	protected Result check() throws Exception
	{
		// TODO check health of stuff here eventually
		return Result.healthy();
	}

}
