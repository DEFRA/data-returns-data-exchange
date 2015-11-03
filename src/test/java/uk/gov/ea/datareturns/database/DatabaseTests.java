package uk.gov.ea.datareturns.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import uk.gov.ea.datareturns.config.PermitDatabaseConfig;
import uk.gov.ea.datareturns.exception.application.DatabaseConfigException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseTests
{
	@Test
	public void testConfigNotSet()
	{
		try
		{
			PermitDatabase.getInstance();
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(DatabaseConfigException.class);
		}
	}

	@Test
	public void testDatabaseNotFound()
	{
		PermitDatabase.setConfig(new PermitDatabaseConfig("/any_location", "and_name.mdb", "any_table", "any_column"));

		try
		{
			PermitDatabase.getInstance();
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(FileUnlocatableException.class);
		}
	}
}
