package uk.gov.ea.datareturns.helper;

import java.util.UUID;

public class DataExchangeHelper
{
	/**
	 * Replace all spaces with underscore and make lowercase
	 * @param returnType
	 * @return
	 */
	public static String makeSchemaName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + ".csvs";
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	public static String generateUniqueFileKey()
	{
		return UUID.randomUUID().toString();
	}
}
