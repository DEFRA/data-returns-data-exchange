package uk.gov.ea.datareturns.resources;

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
	private String emailTo;

	@NotEmpty
	private String emailFrom;

	private String user;
	private String password;
	private boolean tls;
	private String bodyMessage;
	
	public EmailSettings()
	{
		this.host = "";
		this.port = 0;
		this.emailTo = "";
		this.emailFrom = "";
		this.user = "";
		this.password = "";
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
	public String getUser()
	{
		return this.user;
	}

	@JsonProperty
	public void setUser(String user)
	{
		this.user = user;
	}

	@JsonProperty
	public String getPassword()
	{
		return this.password;
	}

	@JsonProperty
	public void setPassword(String password)
	{
		this.password = password;
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