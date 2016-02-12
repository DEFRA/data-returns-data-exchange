package uk.gov.ea.datareturns.helper;

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
}
