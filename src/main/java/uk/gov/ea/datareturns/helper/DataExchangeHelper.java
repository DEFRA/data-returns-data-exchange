package uk.gov.ea.datareturns.helper;

import java.util.UUID;

import javax.xml.transform.Transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExchangeHelper
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeHelper.class);

	/**
	 * Perform XSL transformation
	 * @param xml
	 * @param xslt
	 * @param params
	 * @return
	 */
	public static <T> T transformToResult(String xml, String xslt, Class<T> clazz)
	{
		Transformer transformer = XMLUtilsHelper.createTransformer(xslt);

		T result = XMLUtilsHelper.transformToResult(transformer, xml, clazz);

		return result;
	}

	/**
	 * Replace all spaces with underscore and make lowercase
	 * @param returnType
	 * @return
	 */
	public static String makeSchemaName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + ".xsd";
	}

	public static String makeTranslationsName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + "_translations.xml";
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	public static String generateFileKey()
	{
		return UUID.randomUUID().toString();
	}

}
