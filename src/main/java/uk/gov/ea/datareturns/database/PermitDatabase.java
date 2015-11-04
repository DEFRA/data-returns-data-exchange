package uk.gov.ea.datareturns.database;

import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;

import java.io.File;
import java.io.IOException;

import uk.gov.ea.datareturns.config.PermitDatabaseConfig;
import uk.gov.ea.datareturns.exception.application.DatabaseConfigException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

public class PermitDatabase
{
	private static Database db;
	private static PermitDatabaseConfig config;

	/**
	 * Prevent instantiation 
	 */
	private PermitDatabase()
	{
	}

	public static void setConfig(PermitDatabaseConfig dbConfig)
	{
		config = dbConfig;
	}

	public static PermitDatabaseConfig getConfig()
	{
		return config;
	}

	public static Database getInstance()
	{
		if (config == null)
		{
			throw new DatabaseConfigException("Database configuration not set");
		}

		if (db == null)
		{
			String fileLocation = makeFullPath(config.getLocation(), config.getName());

			try
			{
				db = DatabaseBuilder.open(new File(fileLocation));
			} catch (IOException e)
			{
				throw new FileUnlocatableException(e, "Unable to locate Permit database  '" + fileLocation + "'");
			}

		}

		return db;
	}
}
