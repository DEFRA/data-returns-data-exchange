/**
 *
 */
package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Stores database connection settings
 *
 * @author Sam Gardner-Dell
 *
 */
public class DatabaseSettings {
	@NotEmpty
	private String url;

	@NotEmpty
	private String driverClass;

	@NotEmpty
	private String user;

	@NotEmpty
	private String password;

	@NotEmpty
	private String dialect;

	/**
	 *
	 */
	public DatabaseSettings() {
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return this.driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(final String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return this.dialect;
	}

	/**
	 * @param dialect the dialect to set
	 */
	public void setDialect(final String dialect) {
		this.dialect = dialect;
	}
}
