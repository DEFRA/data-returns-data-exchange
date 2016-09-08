package uk.gov.ea.datareturns.domain.result;

/**
 * Response container for file uploads to the service
 *
 * @author Sam Gardner-Dell
 */
public class UploadResult {
    /** the filename of the file that was uploaded */
    private String fileName;
    /** the unique file key that was generated for the uploaded file */
    private String fileKey;

    /**
     * Default zero-arg constructor (for serialization support)
     */
    @SuppressWarnings("unused")
    public UploadResult() {
    }

    /**
     * Create a new UploadResult for the given filename
     *
     * @param filename the filename to associated with the UploadResult
     */
    public UploadResult(final String filename) {
        this.fileName = filename;
    }

    /**
     * @return the filename to associated with the UploadResult
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Set the filename to associated with the UploadResult
     *
     * @param fileName the filename to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the unique file key that was generated for the uploaded file
     */
    public String getFileKey() {
        return this.fileKey;
    }

    /**
     * Set the unique file key that was generated for the uploaded file
     * @param fileKey the file key to set
     */
    public void setFileKey(final String fileKey) {
        this.fileKey = fileKey;
    }
}
