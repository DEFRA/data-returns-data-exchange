package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author adrianharrison
 * Holds test settings from configuration file
 */
@Configuration
@ConfigurationProperties(prefix="test")
public class TestSettings {
	@NotEmpty
	private String testFilesLocation;

	@NotEmpty
	private String testTimeout;

	@NotEmpty
	private String cleanupAfterTestRun;

	public TestSettings() {
	}

	public String getTestFilesLocation() {
		return this.testFilesLocation;
	}

	public void setTestFilesLocation(final String testFilesLocation) {
		this.testFilesLocation = testFilesLocation;
	}

	public String getTestTimeout() {
		return this.testTimeout;
	}

	public void setTestTimeout(final String testTimeout) {
		this.testTimeout = testTimeout;
	}

	public String getCleanupAfterTestRun() {
		return this.cleanupAfterTestRun;
	}

	public void setCleanupAfterTestRun(final String cleanupAfterTestRun) {
		this.cleanupAfterTestRun = cleanupAfterTestRun;
	}
}