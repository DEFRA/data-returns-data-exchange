package uk.gov.ea.datareturns.domain.io.csv.generic;

public abstract class AbstractCSVRecord {
	private long lineNumber;

	public AbstractCSVRecord() {
	}

	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(final long lineNumber) {
		this.lineNumber = lineNumber;
	}
}