package uk.gov.ea.datareturns.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.domain.dataexchange.EmmaDatabase;
import uk.gov.ea.datareturns.exception.application.DRInvalidPermitNoException;

public abstract class DataExchangeHelper {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeHelper.class);

	private static String NUMERIC_PERMIT_NO_REGEX = "^[0-9]{5,6}$";
	private static Pattern numPermitNoPattern = Pattern.compile(NUMERIC_PERMIT_NO_REGEX);

	private static String ALPHA_NUMERIC_PERMIT_NO_REGEX = "^[A-Za-z][A-Za-z]";
	private static Pattern alphaNnumPermitNoPattern = Pattern.compile(ALPHA_NUMERIC_PERMIT_NO_REGEX);

	public static int DATABASE_LOWER_NUMERIC_BOUNDARY = 69999; // Bizarre qwerk,
																// Not in label
																// range
	public static char DATABASE_UPPER_ALPHA_NUMERIC_BOUNDARY = 'G';

	/**
	 * Number between 5-6 digits long
	 * 
	 * @param permitNo
	 * @return
	 */
	public static boolean isNumericPermitNo(String permitNo) {
		Matcher m = numPermitNoPattern.matcher(permitNo);
		boolean ret = m.find();

		return ret;
	}

	// TODO limited validation for Beta Pilot - needs fully implementing later
	/**
	 * Must start with at least 2 letters.
	 * 
	 * @param permitNo
	 * @return
	 */
	public static boolean isAlphaNumericPermitNo(String permitNo) {
		Matcher m = alphaNnumPermitNoPattern.matcher(permitNo);
		boolean ret = m.find();

		return ret;
	}

	/**
	 * Determines database name from the permit no.
	 * 
	 * @param permitNo
	 * @return
	 */
	public static EmmaDatabase getDatabaseTypeFromPermitNo(String permitNo) {
		if (isNumericPermitNo(permitNo)) {
			if (Integer.parseInt(permitNo) <= DATABASE_LOWER_NUMERIC_BOUNDARY) {
				return EmmaDatabase.LOWER_NUMERIC;
			} else {
				return EmmaDatabase.UPPER_NUMERIC;
			}
		} else if (isAlphaNumericPermitNo(permitNo)) {
			char startLetter = permitNo.toUpperCase().charAt(0);

			if (startLetter <= DATABASE_UPPER_ALPHA_NUMERIC_BOUNDARY) {
				return EmmaDatabase.LOWER_ALPHANUMERIC;
			} else {
				return EmmaDatabase.UPPER_ALPHANUMERIC;
			}
		} else {
			throw new DRInvalidPermitNoException("Permit no '" + permitNo + "' is invalid");
		}
	}
}
