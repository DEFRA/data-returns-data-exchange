package uk.gov.ea.datareturns.domain.io.csv.generic.settings;

import uk.gov.ea.datareturns.domain.io.csv.generic.CSVHeaderValidator;

public class CSVReaderSettings extends AbstractCSVSettings {
	private CSVHeaderValidator headerValidator;

	public CSVReaderSettings() {
		super();
	}

	public CSVReaderSettings(final Character delimiter, final CSVHeaderValidator headerValidator) {
		super(delimiter);
		setHeaderValidator(headerValidator);
	}

	/**
	 * @return the headerValidator
	 */
	public CSVHeaderValidator getHeaderValidator() {
		return this.headerValidator;
	}

	/**
	 * @param headerValidator the headerValidator to set
	 */
	public void setHeaderValidator(final CSVHeaderValidator headerValidator) {
		this.headerValidator = headerValidator;
	}
}
