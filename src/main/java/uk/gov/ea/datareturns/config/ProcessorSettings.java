package uk.gov.ea.datareturns.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Settings specific to the data returns processor classes
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@ConfigurationProperties(prefix = "processor")
public class ProcessorSettings {
	@NotEmpty
	private String outputLocation;

	@NotEmpty
	private List<String> outputMappings;

	@JsonIgnore
	private Map<String, String> outputMappingsMap = null;

	/**
	 * Create a new ProcessorSettings instance
	 */
	public ProcessorSettings() {
	}

	/**
	 * @return the configured output location on the filesystem
	 */
	public String getOutputLocation() {
		return this.outputLocation;
	}

	/**
	 * Set the configured output location on the filesystem
	 *
	 * @param outputLocation the configured output location on the filesystem
	 */
	public void setOutputLocation(final String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * @return the outputMappings
	 */
	public List<String> getOutputMappings() {
		return this.outputMappings;
	}

	/**
	 * @param outputMappings the outputMappings to set
	 */
	public void setOutputMappings(final List<String> outputMappings) {
		this.outputMappings = outputMappings;
	}

	/**
	 * Retrieve the headings/field mappings for the output CSV fileName in map form
	 *
	 * @return a {@link Map} containing the headings to be output (keys) to a set of tokens representing the input fields to be output
	 * under those headings.  Values contain tokens such as {{EA_ID}} which are replaced with the values from the model.
	 */
	public Map<String, String> getOutputMappingsMap() {
		if (this.outputMappingsMap == null) {
			synchronized (this) {
				if (this.outputMappingsMap == null) {
					this.outputMappingsMap = new LinkedHashMap<>();
					final List<String> mappingList = getOutputMappings();
					for (final String mapping : mappingList) {
						final String[] tokens = StringUtils.split(mapping, "=", 2);
						this.outputMappingsMap.put(tokens[0], tokens[1]);
					}
				}
			}
		}
		return this.outputMappingsMap;
	}
}