package uk.gov.ea.datareturns.config;

/**
 * Email settings (Monitorpro)
 *
 */
public class EmailSettings
{
	private String host;
	private int port;
	private String subject;
	private String emailTo;
	private String emailFrom;
	private boolean tls;
	private String bodyMessage;

	public EmailSettings(String host, String port, String subject, String emailTo, String emailFrom, String tls, String bodyMessage)
	{
		this.host = host;
		this.port = Integer.parseInt(port);
		this.emailTo = emailTo;
		this.emailFrom = emailFrom;
		this.tls = Boolean.parseBoolean(tls);
		this.bodyMessage = bodyMessage;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getEmailTo()
	{
		return emailTo;
	}

	public String getEmailFrom()
	{
		return emailFrom;
	}

	public boolean isTls()
	{
		return tls;
	}

	public String getBodyMessage()
	{
		return bodyMessage;
	}
}