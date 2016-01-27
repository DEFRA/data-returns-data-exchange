package uk.gov.ea.datareturns.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailSettings
{
	@NotEmpty
	private String host;

	@Min(1)
	@Max(65535)
	private int port;

	@NotEmpty
	private String subject;

	@NotEmpty
	private String emailTo;

	@NotEmpty
	private String emailFrom;

	private boolean tls;
	private String bodyMessage;
	
	public EmailSettings()
	{
		this.host = "";
		this.port = 0;
		this.emailTo = "";
		this.emailFrom = "";
		this.tls = false;
		this.bodyMessage = "";
	}

	@JsonProperty
	public String getHost()
	{
		return this.host;
	}

	@JsonProperty
	public void setHost(String host)
	{
		this.host = host;
	}

	@JsonProperty
	public int getPort()
	{
		return this.port;
	}

	@JsonProperty
	public void setPort(int port)
	{
		this.port = port;
	}

	@JsonProperty
	public String getSubject()
	{
		return subject;
	}

	@JsonProperty
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	@JsonProperty
	public String getEmailTo()
	{
		return this.emailTo;
	}

	@JsonProperty
	public void setEmailTo(String emailTo)
	{
		this.emailTo = emailTo;
	}

	@JsonProperty
	public String getEmailFrom()
	{
		return this.emailFrom;
	}

	@JsonProperty
	public void setEmailFrom(String emailFrom)
	{
		this.emailFrom = emailFrom;
	}

	@JsonProperty
	public boolean getTls()
	{
		return this.tls;
	}

	@JsonProperty
	public void setTls(boolean tls)
	{
		this.tls = tls;
	}

	@JsonProperty
	public String getBodyMessage()
	{
		return this.bodyMessage;
	}

	@JsonProperty
	public void setBodyMessage(String bodyMessage)
	{
		this.bodyMessage = bodyMessage;
	}
}