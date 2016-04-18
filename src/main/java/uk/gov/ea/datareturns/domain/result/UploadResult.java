package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UploadResult {
	@JsonInclude(Include.NON_NULL)
	private String fileName;

	@JsonInclude(Include.NON_NULL)
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
