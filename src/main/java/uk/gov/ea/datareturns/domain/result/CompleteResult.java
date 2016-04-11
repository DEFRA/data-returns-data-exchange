package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CompleteResult {
	@JacksonXmlProperty(localName = "FileKey")
	private String fileKey;

	@JacksonXmlProperty(localName = "UserEmail")
	private String userEmail;

	public CompleteResult() {

	}

	public CompleteResult(final String fileKey, final String userEmail) {
		this.fileKey = fileKey;
		this.userEmail = userEmail;
	}

	public String getFileKey() {
		return this.fileKey;
	}

	public void setFileKey(final String fileKey) {
		this.fileKey = fileKey;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public void setUserEmail(final String userEmail) {
		this.userEmail = userEmail;
	}

	@JsonIgnore
	public boolean isSendUserEmail() {
		return !"".equals(this.userEmail.trim());
	}
}
