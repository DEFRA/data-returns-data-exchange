/**
 *
 */
package uk.gov.ea.datareturns.config.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import uk.gov.ea.datareturns.domain.model.rules.EaIdType;

/**
 * Configuration values for email messages to Monitor Pro
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@ConfigurationProperties(prefix = "monitorProEmail")
public class MonitorProEmailConfiguration {
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
	public MonitorProEmailConfiguration() {
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(final String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(final String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return this.to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(final String to) {
		this.to = to;
	}

	/**
	 * @return the subjectLowerNumericUniqueId
	 */
	public String getSubjectLowerNumericUniqueId() {
		return this.subjectLowerNumericUniqueId;
	}

	/**
	 * @param subjectLowerNumericUniqueId the subjectLowerNumericUniqueId to set
	 */
	public void setSubjectLowerNumericUniqueId(final String subjectLowerNumericUniqueId) {
		this.subjectLowerNumericUniqueId = subjectLowerNumericUniqueId;
	}

	/**
	 * @return the subjectUpperNumericUniqueId
	 */
	public String getSubjectUpperNumericUniqueId() {
		return this.subjectUpperNumericUniqueId;
	}

	/**
	 * @param subjectUpperNumericUniqueId the subjectUpperNumericUniqueId to set
	 */
	public void setSubjectUpperNumericUniqueId(final String subjectUpperNumericUniqueId) {
		this.subjectUpperNumericUniqueId = subjectUpperNumericUniqueId;
	}

	/**
	 * @return the subjectLowerAlphaNumericUniqueId
	 */
	public String getSubjectLowerAlphaNumericUniqueId() {
		return this.subjectLowerAlphaNumericUniqueId;
	}

	/**
	 * @param subjectLowerAlphaNumericUniqueId the subjectLowerAlphaNumericUniqueId to set
	 */
	public void setSubjectLowerAlphaNumericUniqueId(final String subjectLowerAlphaNumericUniqueId) {
		this.subjectLowerAlphaNumericUniqueId = subjectLowerAlphaNumericUniqueId;
	}

	/**
	 * @return the subjectUpperAlphaNumericUniqueId
	 */
	public String getSubjectUpperAlphaNumericUniqueId() {
		return this.subjectUpperAlphaNumericUniqueId;
	}

	/**
	 * @param subjectUpperAlphaNumericUniqueId the subjectUpperAlphaNumericUniqueId to set
	 */
	public void setSubjectUpperAlphaNumericUniqueId(final String subjectUpperAlphaNumericUniqueId) {
		this.subjectUpperAlphaNumericUniqueId = subjectUpperAlphaNumericUniqueId;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(final String body) {
		this.body = body;
	}

	/**
	 * @return the useTLS
	 */
	public boolean isUseTLS() {
		return this.useTLS;
	}

	/**
	 * @param useTLS the useTLS to set
	 */
	public void setUseTLS(final boolean useTLS) {
		this.useTLS = useTLS;
	}

	/**
	 * Retrieve the appropriate configuration value base on the {@link EaIdType} enumeration instance passed
	 *
	 * @param db the type of Emma database that should be used (internal enum)
	 * @return the text value configured for the type of database being used.
	 */
	public final String getDatabaseName(final EaIdType db) {
		if (EaIdType.LOWER_NUMERIC.equals(db)) {
			return this.subjectLowerNumericUniqueId;
		} else if (EaIdType.UPPER_NUMERIC.equals(db)) {
			return this.subjectUpperNumericUniqueId;
		} else if (EaIdType.LOWER_ALPHANUMERIC.equals(db)) {
			return this.subjectLowerAlphaNumericUniqueId;
		} else if (EaIdType.UPPER_ALPHANUMERIC.equals(db)) {
			return this.subjectUpperAlphaNumericUniqueId;
		} else {
			throw new AssertionError("Unable to determine EMMA database");
		}
	}
}