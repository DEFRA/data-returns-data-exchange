package uk.gov.ea.datareturns.domain.io.csv.generic;

public abstract class AbstractCSVRecord {
	private long lineNumber;

	public AbstractCSVRecord() {
	}

	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}
}