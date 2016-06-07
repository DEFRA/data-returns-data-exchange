package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Holds test settings from configuration fileName
 * @author adrianharrison
 */
@Configuration
@ConfigurationProperties(prefix = "test")
public class TestSettings {
	@NotEmpty
	private String testFilesLocation;
	@NotEmpty
	private String testTimeout;

	/**
	 * Create a new TestSettings instance
	 */
	public TestSettings() {
	}

	/**
	 * @return the location of test files within the package structure
	 */
	public String getTestFilesLocation() {
		return this.testFilesLocation;
	}

	/**
	 * Set the location of test files within the package structure
	 *
	 * @param testFilesLocation the location to set
	 */
	public void setTestFilesLocation(final String testFilesLocation) {
		this.testFilesLocation = testFilesLocation;
	}

	/**
	 * @return the test timeout for request to the RESTful service
	 */
	public String getTestTimeout() {
		return this.testTimeout;
	}

	/**
	 * Set the test timeout for request to the RESTful service
	 * @param testTimeout the timeout to use
	 */
	public void setTestTimeout(final String testTimeout) {
		this.testTimeout = testTimeout;
	}
}