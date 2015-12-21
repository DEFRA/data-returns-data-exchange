package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author adrianharrison
 * Holds miscellaneous settings from configuration file
 */
public class MiscSettings
{
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

	private String testFilesLocation;
	private String debugMode;
	private String defaultTestTimeout;
	private String debugTestTimeout;
	private String cleanupAfterTestRun;

	public MiscSettings()
	{
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

	public String getTestFilesLocation()
	{
		return testFilesLocation;
	}

	public void setTestFilesLocation(String testFilesLocation)
	{
		this.testFilesLocation = testFilesLocation;
	}

	public String getDebugMode()
	{
		return debugMode;
	}

	public void setDebugMode(String debugMode)
	{
		this.debugMode = debugMode;
	}

	public String getDefaultTestTimeout()
	{
		return defaultTestTimeout;
	}

	public void setDefaultTestTimeout(String defaultTestTimeout)
	{
		this.defaultTestTimeout = defaultTestTimeout;
	}

	public String getDebugTestTimeout()
	{
		return debugTestTimeout;
	}

	public void setDebugTestTimeout(String debugTestTimeout)
	{
		this.debugTestTimeout = debugTestTimeout;
	}

	public String getCleanupAfterTestRun()
	{
		return cleanupAfterTestRun;
	}

	public void setCleanupAfterTestRun(String cleanupAfterTestRun)
	{
		this.cleanupAfterTestRun = cleanupAfterTestRun;
	}
}