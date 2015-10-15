package uk.gov.ea.datareturns.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestConfiguration extends DataExchangeConfiguration
{
	public TestConfiguration()
	{
		super();
		
		// TODO need to be read from configuration file or mocked?
		EmailSettings emailSettings = new EmailSettings();
		emailSettings.setHost("127.0.0.1");
		emailSettings.setPort(9999);
		emailSettings.setEmailTo("import@monitorpro.com");
		emailSettings.setEmailFrom("data_returns@environment-agency.gov.uk");
		emailSettings.setTls(false);
		emailSettings.setBodyMessage("Data Returns Import file received");

		setEmailsettings(emailSettings);
	}
	
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
