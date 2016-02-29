package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author adrianharrison
 * Holds miscellaneous settings from configuration file
 */
public class MiscSettings
{
	@NotEmpty
	private String environment;

	@NotEmpty
	private String uploadedLocation;

	@NotEmpty
	private String schemaLocation;

	@NotEmpty
	private String outputLocation;

	@NotEmpty
	private String xsltLocation;

	@NotEmpty
	private String xmlLocation;

	@NotEmpty
	private String csvSeparator;

	@NotEmpty
	private String debugMode;

	public MiscSettings()
	{
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getUploadedLocation()
	{
		return uploadedLocation;
	}

	public void setUploadedLocation(String uploadedLocation)
	{
		this.uploadedLocation = uploadedLocation;
	}

	public String getSchemaLocation()
	{
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation)
	{
		this.schemaLocation = schemaLocation;
	}

	public String getOutputLocation()
	{
		return outputLocation;
	}

	public void setOutputLocation(String outputLocation)
	{
		this.outputLocation = outputLocation;
	}

	public String getXsltLocation()
	{
		return xsltLocation;
	}

	public void setXsltLocation(String xsltLocation)
	{
		this.xsltLocation = xsltLocation;
	}

	public String getXmlLocation()
	{
		return xmlLocation;
	}

	public void setXmlLocation(String xmlLocation)
	{
		this.xmlLocation = xmlLocation;
	}

	public String getCsvSeparator()
	{
		return csvSeparator;
	}

	public void setCsvSeparator(String csvSeparator)
	{
		this.csvSeparator = csvSeparator;
	}

	public String getDebugMode()
	{
		return debugMode;
	}

	public void setDebugMode(String debugMode)
	{
		this.debugMode = debugMode;
	}
}