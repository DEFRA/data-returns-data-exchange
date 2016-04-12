/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv.generic.settings;

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
	public AbstractCSVSettings(final Character delimiter) {
		this();
		this.delimiter = delimiter;
	}

	/**
	 * @return the delimiter
	 */
	public Character getDelimiter() {
		return this.delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(final Character delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the trimWhitespace
	 */
	public boolean isTrimWhitespace() {
		return this.trimWhitespace;
	}

	/**
	 * @param trimWhitespace the trimWhitespace to set
	 */
	public void setTrimWhitespace(final boolean trimWhitespace) {
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
		CSVFormat format = CSVFormat.RFC4180.withHeader();
		if (this.delimiter != null) {
			format = format.withDelimiter(this.delimiter);
		}
		format = format.withIgnoreSurroundingSpaces(isTrimWhitespace());
		return format;
	}
}
