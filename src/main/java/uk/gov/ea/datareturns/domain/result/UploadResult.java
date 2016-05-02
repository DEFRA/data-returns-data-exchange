package uk.gov.ea.datareturns.domain.result;

public class UploadResult {
	private String fileName;

	private String fileKey;

	public UploadResult() {
	}

	public UploadResult(final String filename) {
		this.fileName = filename;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileKey() {
		return this.fileKey;
	}

	public void setFileKey(final String fileKey) {
		this.fileKey = fileKey;
	}
}
