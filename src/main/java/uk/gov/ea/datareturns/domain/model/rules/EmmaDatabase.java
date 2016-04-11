package uk.gov.ea.datareturns.domain.model.rules;

public enum EmmaDatabase {
	/** Database used for permit numbers using lowercase numeric identifier */
	LOWER_NUMERIC,
	/** Database used for permit numbers using upppercase numeric identifier */
	UPPER_NUMERIC,
	/** Database used for permit numbers using lowercase alphanumeric identifier */
	LOWER_ALPHANUMERIC,
	/** Database used for permit numbers using uppercase alphanumeric identifier */
	UPPER_ALPHANUMERIC;
}
