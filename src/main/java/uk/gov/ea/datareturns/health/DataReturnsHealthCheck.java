package uk.gov.ea.datareturns.health;

import com.codahale.metrics.health.HealthCheck;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.PropertyMap;

public class DataReturnsHealthCheck extends HealthCheck
{
	private Database db;
	
	public DataReturnsHealthCheck(Database db)
	{
		this.db = db;
	}

	@Override
	protected Result check() throws Exception
	{
		// TODO is there a better way to check?
		PropertyMap props = db.getDatabaseProperties();
		
		return (props == null ? Result.unhealthy("Permit database has a problem") : Result.healthy());
	}

}
