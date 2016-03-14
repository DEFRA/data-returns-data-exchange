/**
 * 
 */
package uk.gov.ea.datareturns.domain.io.csv.settings;

import org.apache.commons.csv.CSVFormat;

/**
 * CSV settings that are common to both the reader and the writer
 * 
 * @author Sam Gardner-Dell
 *
 */
public class AbstractCSVSettings {
	/** The field delimiter character */
	private Character delimiter;

	/** Trim whitespace around values */
	private boolean trimWhitespace;

	public AbstractCSVSettings() {
	}
	
	/**
	 * 
	 */
	public AbstractCSVSettings(Character delimiter) {
		this.delimiter = delimiter;
	}


	/**
	 * @return the delimiter
	 */
	public Character getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(Character delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the trimWhitespace
	 */
	public boolean isTrimWhitespace() {
		return trimWhitespace;
	}

	/**
	 * @param trimWhitespace the trimWhitespace to set
	 */
	public void setTrimWhitespace(boolean trimWhitespace) {
		this.trimWhitespace = trimWhitespace;
	}

	/**
	 * Internal method to create an appropriate {@link CSVFormat} instance for use with apache commons csv parser
	 * 
	 * Note, this is an override point.  Subclasses should always call super.getCSVFormat() before making further changes.
	 * 
	 * @return a CSVFormat with the appropriate settings.
	 */
	public CSVFormat getCSVFormat() {
		CSVFormat format = CSVFormat.EXCEL.withHeader();
		if (delimiter != null) {
			format = format.withDelimiter(delimiter);
		}
		return format;
	}
}
