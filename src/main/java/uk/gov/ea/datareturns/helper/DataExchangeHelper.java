package uk.gov.ea.datareturns.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.application.DRInvalidPermitNoException;

public class DataExchangeHelper
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeHelper.class);

	private static String NUMERIC_PERMIT_NO_REGEX = "^[0-9]{5,6}$";
	private static Pattern numPermitNoPattern = Pattern.compile(NUMERIC_PERMIT_NO_REGEX);

	private static String ALPHA_NUMERIC_PERMIT_NO_REGEX = "^[A-Za-z][A-Za-z]";
	private static Pattern alphaNnumPermitNoPattern = Pattern.compile(ALPHA_NUMERIC_PERMIT_NO_REGEX);

	// TODO from configuration file?
	public static String DATABASE_LOWER_NUMERIC_NAME = "EA_LP_10000_TO_69000_LIST";
	public static int DATABASE_LOWER_NUMERIC_BOUNDARY = 69999; // Bizarre qwerk, Not in label range
	public static String DATABASE_UPPER_NUMERIC_NAME = "EA_LP_70000_ABOVE_LIST";
	public static String DATABASE_LOWER_ALPHA_NUMERIC_NAME = "EA_LP_AA_TO_GZ_LIST";
	public static char DATABASE_UPPER_ALPHA_NUMERIC_BOUNDARY = 'G';
	public static String DATABASE_UPPER_ALPHA_NUMERIC_NAME = "EA_LP_HA_TO_ZZ_LIST";

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
	 * Number between 5-6 digits long
	 * @param permitNo
	 * @return
	 */
	public static boolean isNumericPermitNo(String permitNo)
	{
		Matcher m = numPermitNoPattern.matcher(permitNo);
		boolean ret = m.find();

		return ret;
	}

	// TODO limited validation for Beta Pilot - needs fully implementing later
	/**
	 * Must start with at least 2 letters. 
	 * @param permitNo
	 * @return
	 */
	public static boolean isAlphaNumericPermitNo(String permitNo)
	{
		Matcher m = alphaNnumPermitNoPattern.matcher(permitNo);
		boolean ret = m.find();

		return ret;
	}

	/**
	 * Determines database name from the permit no. 
	 * @param permitNo
	 * @return
	 */
	// TODO could do in xslt?
	public static String getDatabaseNameFromPermitNo(String permitNo)
	{
		if (isNumericPermitNo(permitNo))
		{
			if (Integer.parseInt(permitNo) <= DATABASE_LOWER_NUMERIC_BOUNDARY)
			{
				return DATABASE_LOWER_NUMERIC_NAME;
			} else
			{
				return DATABASE_UPPER_NUMERIC_NAME;
			}
		} else if (isAlphaNumericPermitNo(permitNo))
		{
			char startLetter = permitNo.toUpperCase().charAt(0);

			if (startLetter <= DATABASE_UPPER_ALPHA_NUMERIC_BOUNDARY)
			{
				return DATABASE_LOWER_ALPHA_NUMERIC_NAME;
			} else
			{
				return DATABASE_UPPER_ALPHA_NUMERIC_NAME;
			}
		} else
		{
			throw new DRInvalidPermitNoException("Permit no '" + permitNo + "' is invalid");
		}
	}
}
