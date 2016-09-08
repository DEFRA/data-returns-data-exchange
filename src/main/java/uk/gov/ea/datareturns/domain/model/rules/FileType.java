package uk.gov.ea.datareturns.domain.model.rules;

/**
 * Enumeration of file types recognised by the data returns service
 *
 * @author Sam Gardner-Dell
 */
public enum FileType {
    /** Comma separated values files */
    CSV("csv"),
    /** Extensible markup language files */
    XML("xml");

    private final String extension;

    /**
     * Create a new FileType
     * @param extension the file extension
     */
    FileType(final String extension) {
        this.extension = extension;
    }

    /**
     * Retrieve the file extension for the file type
     *
     * @return the file extension
     */
    public String getExtension() {
        return this.extension;
    }
}
