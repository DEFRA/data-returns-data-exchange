package uk.gov.ea.datareturns.config;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataExchangeConfiguration extends Configuration
{
	@Valid
	@NotNull
	private MiscSettings miscSettings = new MiscSettings();

	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	 @JsonProperty("database")
	public void setDatabase(DataSourceFactory database)
	{
		this.database = database;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory()
	{
		return database;
	}
	
	@Valid
	@NotNull
	private EmailSettings emailsettings = new EmailSettings();

	@JsonProperty("misc")
	public MiscSettings getMiscSettings()
	{
		return miscSettings;
	}

	@JsonProperty("misc")
	public void setMiscSettings(MiscSettings miscSettings)
	{
		this.miscSettings = miscSettings;
	}

	@JsonProperty("email")
	public EmailSettings getEmailsettings()
	{
		return emailsettings;
	}

	@JsonProperty("email")
	public void setEmailsettings(EmailSettings emailsettings)
	{
		this.emailsettings = emailsettings;
	}
}
