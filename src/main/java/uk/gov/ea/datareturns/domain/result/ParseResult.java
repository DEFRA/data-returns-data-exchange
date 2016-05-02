package uk.gov.ea.datareturns.domain.result;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;

/**
 * Simple class to return the result of a successful parse to the client
 */
public class ParseResult {
	private String permitNumber;

	private String siteName;
	
	public ParseResult() {
		
	}

	public ParseResult(List<MonitoringDataRecord> records) {
		final Set<String> permitNumbers = new LinkedHashSet<>();
		final Set<String> siteNames = new LinkedHashSet<>();

		for (final MonitoringDataRecord record : records) {
			permitNumbers.add(record.getPermitNumber());
			if (StringUtils.isNotEmpty(record.getSiteName())) {
				siteNames.add(record.getSiteName());
			}
		}

		setPermitNumber(StringUtils.join(permitNumbers, ", "));
		setSiteName(StringUtils.join(siteNames, ", "));
	}

	/**
	 * @return the permitNumber
	 */
	public String getPermitNumber() {
		return this.permitNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * @param permitNumber
	 *            the permitNumber to set
	 */
	public void setPermitNumber(final String permitNumber) {
		this.permitNumber = permitNumber;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}
}
