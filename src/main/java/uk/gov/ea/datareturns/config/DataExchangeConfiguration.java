package uk.gov.ea.datareturns.config;

import io.dropwizard.Configuration;

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
	private EmailSettings emailsettings = new EmailSettings();

	@Valid
	@NotNull
	private PermitDatabaseConfig permitDatabaseConfig = new PermitDatabaseConfig();

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

	@JsonProperty("permitDatabase")
	public PermitDatabaseConfig getPermitDatabaseConfig()
	{
		return permitDatabaseConfig;
	}

	@JsonProperty("permitDatabase")
	public void setPermitDatabaseConfig(PermitDatabaseConfig permitDatabaseConfig)
	{
		this.permitDatabaseConfig = permitDatabaseConfig;
	}
}
