/**
 * 
 */
package uk.gov.ea.datareturns.config.email;

import uk.gov.ea.datareturns.domain.dataexchange.EmmaDatabase;
import uk.gov.ea.datareturns.exception.system.DRSystemException;

/**
 * Configuration values for email messages to Monitor Pro
 * 
 * @author Sam Gardner-Dell
 */
public class MonitorProEmailSettings {
	private String host;
	private int port;
	private String from;
	private String to;
	private String subjectLowerNumericUniqueId;
	private String subjectUpperNumericUniqueId;
	private String subjectLowerAlphaNumericUniqueId;
	private String subjectUpperAlphaNumericUniqueId;
	private String body;
	private boolean useTLS;

	/**
	 * 
	 */
	public MonitorProEmailSettings() {
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the subjectLowerNumericUniqueId
	 */
	public String getSubjectLowerNumericUniqueId() {
		return subjectLowerNumericUniqueId;
	}

	/**
	 * @param subjectLowerNumericUniqueId the subjectLowerNumericUniqueId to set
	 */
	public void setSubjectLowerNumericUniqueId(String subjectLowerNumericUniqueId) {
		this.subjectLowerNumericUniqueId = subjectLowerNumericUniqueId;
	}

	/**
	 * @return the subjectUpperNumericUniqueId
	 */
	public String getSubjectUpperNumericUniqueId() {
		return subjectUpperNumericUniqueId;
	}

	/**
	 * @param subjectUpperNumericUniqueId the subjectUpperNumericUniqueId to set
	 */
	public void setSubjectUpperNumericUniqueId(String subjectUpperNumericUniqueId) {
		this.subjectUpperNumericUniqueId = subjectUpperNumericUniqueId;
	}

	/**
	 * @return the subjectLowerAlphaNumericUniqueId
	 */
	public String getSubjectLowerAlphaNumericUniqueId() {
		return subjectLowerAlphaNumericUniqueId;
	}

	/**
	 * @param subjectLowerAlphaNumericUniqueId the subjectLowerAlphaNumericUniqueId to set
	 */
	public void setSubjectLowerAlphaNumericUniqueId(String subjectLowerAlphaNumericUniqueId) {
		this.subjectLowerAlphaNumericUniqueId = subjectLowerAlphaNumericUniqueId;
	}

	/**
	 * @return the subjectUpperAlphaNumericUniqueId
	 */
	public String getSubjectUpperAlphaNumericUniqueId() {
		return subjectUpperAlphaNumericUniqueId;
	}

	/**
	 * @param subjectUpperAlphaNumericUniqueId the subjectUpperAlphaNumericUniqueId to set
	 */
	public void setSubjectUpperAlphaNumericUniqueId(String subjectUpperAlphaNumericUniqueId) {
		this.subjectUpperAlphaNumericUniqueId = subjectUpperAlphaNumericUniqueId;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the useTLS
	 */
	public boolean isUseTLS() {
		return useTLS;
	}

	/**
	 * @param useTLS the useTLS to set
	 */
	public void setUseTLS(boolean useTLS) {
		this.useTLS = useTLS;
	}
	
	
	/**
	 * Retrieve the appropriate configuration value base on the {@link EmmaDatabase} enumeration instance passed
	 * 
	 * @param db the type of Emma database that should be used (internal enum)
	 * @return the text value configured for the type of database being used.
	 */
	public final String getDatabaseName(EmmaDatabase db) {
		if (EmmaDatabase.LOWER_NUMERIC.equals(db)) {
			return subjectLowerNumericUniqueId;
		} else if (EmmaDatabase.UPPER_NUMERIC.equals(db)) {
			return subjectUpperNumericUniqueId;
		} else if (EmmaDatabase.LOWER_ALPHANUMERIC.equals(db)) {
			return subjectLowerAlphaNumericUniqueId;
		} else if (EmmaDatabase.UPPER_ALPHANUMERIC.equals(db)) {
			return subjectUpperAlphaNumericUniqueId;
		} else {
			throw new DRSystemException("Unable to determine EMMA database");
		}
	}
}