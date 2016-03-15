package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

import uk.gov.ea.datareturns.domain.dataexchange.EmmaDatabase;
import uk.gov.ea.datareturns.exception.system.DRSystemException;

public class EmmaDatabaseSettings {
	@NotEmpty private String databaseLowerNumericName;
	@NotEmpty private String databaseUpperNumericName;
	@NotEmpty private String databaseLowerAlphaNumericName;
	@NotEmpty private String databaseUpperAlphaNumericName;
	
	public EmmaDatabaseSettings() {
	}

	/**
	 * @return the databaseLowerNumericName
	 */
	public String getDatabaseLowerNumericName() {
		return databaseLowerNumericName;
	}

	/**
	 * @param databaseLowerNumericName the databaseLowerNumericName to set
	 */
	public void setDatabaseLowerNumericName(String databaseLowerNumericName) {
		this.databaseLowerNumericName = databaseLowerNumericName;
	}

	/**
	 * @return the databaseUpperNumericName
	 */
	public String getDatabaseUpperNumericName() {
		return databaseUpperNumericName;
	}

	/**
	 * @param databaseUpperNumericName the databaseUpperNumericName to set
	 */
	public void setDatabaseUpperNumericName(String databaseUpperNumericName) {
		this.databaseUpperNumericName = databaseUpperNumericName;
	}

	/**
	 * @return the databaseLowerAlphaNumericName
	 */
	public String getDatabaseLowerAlphaNumericName() {
		return databaseLowerAlphaNumericName;
	}

	/**
	 * @param databaseLowerAlphaNumericName the databaseLowerAlphaNumericName to set
	 */
	public void setDatabaseLowerAlphaNumericName(String databaseLowerAlphaNumericName) {
		this.databaseLowerAlphaNumericName = databaseLowerAlphaNumericName;
	}

	/**
	 * @return the databaseUpperAlphaNumericName
	 */
	public String getDatabaseUpperAlphaNumericName() {
		return databaseUpperAlphaNumericName;
	}

	/**
	 * @param databaseUpperAlphaNumericName the databaseUpperAlphaNumericName to set
	 */
	public void setDatabaseUpperAlphaNumericName(String databaseUpperAlphaNumericName) {
		this.databaseUpperAlphaNumericName = databaseUpperAlphaNumericName;
	}
	
	/**
	 * Retrieve the appropriate configuration value base on the {@link EmmaDatabase} enumeration instance passed
	 * 
	 * @param db the type of Emma database that should be used (internal enum)
	 * @return the text value configured for the type of database being used.
	 */
	public final String getDatabaseName(EmmaDatabase db) {
		if (EmmaDatabase.LOWER_NUMERIC.equals(db)) {
			return databaseLowerNumericName;
		} else if (EmmaDatabase.UPPER_NUMERIC.equals(db)) {
			return databaseUpperNumericName;
		} else if (EmmaDatabase.LOWER_ALPHANUMERIC.equals(db)) {
			return databaseLowerAlphaNumericName;
		} else if (EmmaDatabase.UPPER_ALPHANUMERIC.equals(db)) {
			return databaseUpperAlphaNumericName;
		} else {
			throw new DRSystemException("Unable to determine EMMA database");
		}
	}
}