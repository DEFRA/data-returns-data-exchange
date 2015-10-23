package uk.gov.ea.datareturns.helper;

import java.util.UUID;

public class DataExchangeHelper
{
	public static String makeSchemaName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + ".csvs";
	}

	public static String generateUniqueFileKey()
	{
		return UUID.randomUUID().toString();
	}
}
