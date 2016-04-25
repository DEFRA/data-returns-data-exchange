package uk.gov.ea.datareturns.domain.model.rules;

import org.apache.commons.lang3.StringUtils;

public enum EmmaDatabase {
	/** Database used for permit numbers using lowercase numeric identifier */
	LOWER_NUMERIC,
	/** Database used for permit numbers using upppercase numeric identifier */
	UPPER_NUMERIC,
	/** Database used for permit numbers using lowercase alphanumeric identifier */
	LOWER_ALPHANUMERIC,
	/** Database used for permit numbers using uppercase alphanumeric identifier */
	UPPER_ALPHANUMERIC;

	public static int NUMERIC_BOUNDARY = 70000;
	public static char ALPHA_NUMERIC_BOUNDARY = 'H';

	
	public static EmmaDatabase forUniqueId(String uniqueId) {
		EmmaDatabase db = null;
		if (StringUtils.isNotEmpty(uniqueId)) {
			try {
				int numericPermit = Integer.parseInt(uniqueId);
				db = numericPermit < NUMERIC_BOUNDARY ? LOWER_NUMERIC : UPPER_NUMERIC;
			} catch (NumberFormatException e) {
				final char startLetter = uniqueId.toUpperCase().charAt(0);
				db = startLetter < ALPHA_NUMERIC_BOUNDARY ? LOWER_ALPHANUMERIC : UPPER_ALPHANUMERIC;
			}
		}
		return db;
	}
}
