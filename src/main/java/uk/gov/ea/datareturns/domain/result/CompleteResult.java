package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CompleteResult
{
	@JacksonXmlProperty(localName = "FileKey")
	private String fileKey;

	@JacksonXmlProperty(localName = "UserEmail")
	private String userEmail;

	public String getFileKey()
	{
		return fileKey;
	}

	public void setFileKey(String fileKey)
	{
		this.fileKey = fileKey;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	@JsonIgnore
	public boolean isSendUserEmail()
	{
		return !"".equals(userEmail.trim());
	}
}
