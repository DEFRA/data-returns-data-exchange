package uk.gov.ea.datareturns.domain.io.csv.generic;

/**
 * Superclass for records parsed from CSV files
 *
 * @author Sam Gardner-Dell
 */
public abstract class AbstractCSVRecord {
    private long lineNumber;

    /**
     * Create a new {@link AbstractCSVRecord}
     */
    protected AbstractCSVRecord() {
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