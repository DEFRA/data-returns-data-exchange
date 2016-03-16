package uk.gov.ea.datareturns.domain.io.csv.settings;

import uk.gov.ea.datareturns.domain.io.csv.CSVHeaderValidator;

public class CSVReaderSettings extends AbstractCSVSettings {
	private CSVHeaderValidator headerValidator;
	
	
	public CSVReaderSettings() {
		super();
	}

	public CSVReaderSettings(Character delimiter, CSVHeaderValidator headerValidator) {
		super(delimiter);
		setHeaderValidator(headerValidator);
	}

	/**
	 * @return the headerValidator
	 */
	public CSVHeaderValidator getHeaderValidator() {
		return headerValidator;
	}

	/**
	 * @param headerValidator the headerValidator to set
	 */
	public void setHeaderValidator(CSVHeaderValidator headerValidator) {
		this.headerValidator = headerValidator;
	}
}
