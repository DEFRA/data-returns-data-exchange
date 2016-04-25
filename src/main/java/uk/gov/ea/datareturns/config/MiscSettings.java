package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author adrianharrison
 * Holds miscellaneous settings from configuration file
 */
public class MiscSettings {
	@NotEmpty
	private String uploadedLocation;

	@NotEmpty
	private String outputLocation;

	private boolean debugMode;

	public MiscSettings() {
	}

	public String getUploadedLocation() {
		return this.uploadedLocation;
	}

	public void setUploadedLocation(final String uploadedLocation) {
		this.uploadedLocation = uploadedLocation;
	}

	public String getOutputLocation() {
		return this.outputLocation;
	}

	public void setOutputLocation(final String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return this.debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(final boolean debugMode) {
		this.debugMode = debugMode;
	}
}