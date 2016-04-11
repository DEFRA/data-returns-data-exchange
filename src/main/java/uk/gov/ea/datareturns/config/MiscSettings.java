package uk.gov.ea.datareturns.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author adrianharrison
 * Holds miscellaneous settings from configuration file
 */
public class MiscSettings {
	@NotEmpty
	private String uploadedLocation;

	@NotEmpty
	private String outputLocation;

	@NotEmpty
	private String csvSeparator;

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

	public String getCsvSeparator() {
		return this.csvSeparator;
	}

	@JsonIgnore
	public Character getCSVSeparatorCharacter() {
		// Use delimiter from configuration (need first character from string)
		return StringUtils.isNotEmpty(getCsvSeparator()) ? getCsvSeparator().charAt(0) : null;
	}

	public void setCsvSeparator(final String csvSeparator) {
		this.csvSeparator = csvSeparator;
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