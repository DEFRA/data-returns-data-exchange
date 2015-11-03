package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MiscSettings
{
	@NotEmpty
	private String fileUploadLocation;

	@NotEmpty
	private String schemaFileLocation;

	public MiscSettings()
	{
		this.fileUploadLocation = "";
		this.schemaFileLocation = "";
	}

	@JsonProperty
	public String getFileUploadLocation()
	{
		return fileUploadLocation;
	}

	@JsonProperty
	public void setFileUploadLocation(String fileUploadLocation)
	{
		this.fileUploadLocation = fileUploadLocation;
	}

	@JsonProperty
	public String getSchemaFileLocation()
	{
		return schemaFileLocation;
	}

	@JsonProperty
	public void setSchemaFileLocation(String schemaFileLocation)
	{
		this.schemaFileLocation = schemaFileLocation;
	}
}