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
	/** Should the writer output "null" into the CSV when a null bean value is encountered, default behaviour is to write an empty string */
	private boolean writeNullValues = false;
	
	/**
	 * @param delimiter
	 */
	public CSVWriterSettings(Character delimiter, List<String> headers) {
		super(delimiter);
		setHeaders(headers);
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
	
	/**
	 * @return the writeNullValues
	 */
	public boolean isWriteNullValues() {
		return writeNullValues;
	}

	/**
	 * @param writeNullValues the writeNullValues to set
	 */
	public void setWriteNullValues(boolean writeNullValues) {
		this.writeNullValues = writeNullValues;
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
