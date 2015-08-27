package uk.gov.ea.datareturns.config;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataExchangeConfiguration extends Configuration
{
	@Valid
	@NotNull
	private EmailSettings emailsettings = new EmailSettings();

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
