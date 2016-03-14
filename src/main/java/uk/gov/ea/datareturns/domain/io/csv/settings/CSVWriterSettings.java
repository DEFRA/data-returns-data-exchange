/**
 * 
 */
package uk.gov.ea.datareturns.domain.io.csv.settings;

import java.util.List;

import org.apache.commons.csv.CSVFormat;

import uk.gov.ea.datareturns.domain.io.csv.CSVWriter;

/**
 * Settings for the {@link CSVWriter}
 * 
 * @author Sam Gardner-Dell
 */
public class CSVWriterSettings extends AbstractCSVSettings {
	private List<String> headers;

	/**
	 * @param delimiter
	 */
	public CSVWriterSettings(Character delimiter, List<String> headers) {
		super(delimiter);
		this.headers = headers;
	}

	/**
	 * @return the headers
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}


	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.io.csv.settings.AbstractCSVSettings#getCSVFormat()
	 */
	@Override
	public CSVFormat getCSVFormat() {
		CSVFormat format = super.getCSVFormat();
		format = format.withHeader(this.headers.toArray(new String[headers.size()]));
		return format;
	}
}
