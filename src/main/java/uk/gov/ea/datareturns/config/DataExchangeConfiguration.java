package uk.gov.ea.datareturns.config;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import uk.gov.ea.datareturns.config.email.MonitorProEmailSettings;
import uk.gov.ea.datareturns.config.storage.StorageSettings;

public class DataExchangeConfiguration extends Configuration {
	@Valid
	@NotNull
	@JsonProperty("misc")
	private MiscSettings miscSettings = new MiscSettings();

	@Valid
	@NotNull
	@JsonProperty("test")
	private TestSettings testSettings = new TestSettings();

	@Valid
	@NotNull
	@JsonProperty("database")
	private DataSourceFactory database;

	@Valid
	@NotNull
	@JsonProperty("monitorProEmail")
	private MonitorProEmailSettings monitorProEmailSettings = new MonitorProEmailSettings();
	
	@Valid
	@NotNull
	@JsonProperty("storage")
	private StorageSettings storageSettings = new StorageSettings();

	public DataExchangeConfiguration() {
	}

	/**
	 * @return the miscSettings
	 */
	public MiscSettings getMiscSettings() {
		return miscSettings;
	}

	/**
	 * @param miscSettings the miscSettings to set
	 */
	public void setMiscSettings(MiscSettings miscSettings) {
		this.miscSettings = miscSettings;
	}

	/**
	 * @return the testSettings
	 */
	public TestSettings getTestSettings() {
		return testSettings;
	}

	/**
	 * @param testSettings the testSettings to set
	 */
	public void setTestSettings(TestSettings testSettings) {
		this.testSettings = testSettings;
	}

	/**
	 * @return the database
	 */
	public DataSourceFactory getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(DataSourceFactory database) {
		this.database = database;
	}

	/**
	 * @return the monitorProEmailSettings
	 */
	public MonitorProEmailSettings getMonitorProEmailSettings() {
		return monitorProEmailSettings;
	}

	/**
	 * @param monitorProEmailSettings the monitorProEmailSettings to set
	 */
	public void setMonitorProEmailSettings(MonitorProEmailSettings monitorProEmailSettings) {
		this.monitorProEmailSettings = monitorProEmailSettings;
	}

	/**
	 * @return the storageSettings
	 */
	public StorageSettings getStorageSettings() {
		return storageSettings;
	}

	/**
	 * @param storageSettings the storageSettings to set
	 */
	public void setStorageSettings(StorageSettings storageSettings) {
		this.storageSettings = storageSettings;
	}
}