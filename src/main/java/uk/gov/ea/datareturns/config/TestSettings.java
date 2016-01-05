package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author adrianharrison
 * Holds test settings from configuration file
 */
public class TestSettings
{
	@NotEmpty
	private String testFilesLocation;

	@NotEmpty
	private String testTimeout;

	@NotEmpty
	private String cleanupAfterTestRun;

	public TestSettings()
	{
	}

	public String getTestFilesLocation()
	{
		return testFilesLocation;
	}

	public void setTestFilesLocation(String testFilesLocation)
	{
		this.testFilesLocation = testFilesLocation;
	}

	public String getTestTimeout()
	{
		return testTimeout;
	}

	public void setTestTimeout(String testTimeout)
	{
		this.testTimeout = testTimeout;
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