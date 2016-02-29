package uk.gov.ea.datareturns.config;

import static uk.gov.ea.datareturns.helper.CommonHelper.getEnvVar;
import static uk.gov.ea.datareturns.helper.CommonHelper.isLocalEnvironment;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataExchangeConfiguration extends Configuration
{
	public static final String ENV_VAR_REDIS_HOST = "DR_REDIS_HOST";
	public static final String ENV_VAR_REDIS_PORT = "DR_REDIS_PORT";
	public static final String ENV_VAR_DR_S3_TYPE = "DR_S3_TYPE";
	public static final String ENV_VAR_DR_S3_HOST = "DR_S3_HOST";
	public static final String ENV_VAR_DR_S3_PORT = "DR_S3_PORT";

	public static final String ENV_VAR_MONITOR_PRO_HOST = "DR_MONITOR_PRO_HOST";
	public static final String ENV_VAR_MONITOR_PRO_PORT = "DR_MONITOR_PRO_PORT";
	public static final String ENV_VAR_MONITOR_EMAIL_TO = "DR_MONITOR_PRO_EMAIL_TO";
	public static final String ENV_VAR_MONITOR_EMAIL_FROM = "DR_MONITOR_PRO_EMAIl_FROM";
	public static final String ENV_VAR_MONITOR_TLS = "DR_MONITOR_PRO_TLS";
	public static final String ENV_VAR_MONITOR_BODY_MESSAGE = "DR_MONITOR_PRO_BODY_MESSAGE";

	public static final String ENV_VAR_DATABASE_URL = "DR_DATABASE_URL";
	public static final String ENV_VAR_DATABASE_USER = "DR_DATABASE_USER";
	public static final String ENV_VAR_DATABASE_PASSWORD = "DR_DATABASE_PASSWORD";
	public static final String ENV_VAR_DATABASE_DRIVER_CLASS = "DR_DATABASE_DRIVER_CLASS";

	@Valid
	@NotNull
	private MiscSettings miscSettings = new MiscSettings();

	@Valid
	@NotNull
	private EmmaDatabaseSettings emmaDatabaseSettings = new EmmaDatabaseSettings();
	
	@Valid
	@NotNull
	private TestSettings testSettings = new TestSettings();

	private FileStorageSettings fileStorageSettings;
	private S3ProxySettings s3Settings;
	private EmailSettings emailsettings;

	private DataSourceFactory database;

	public DataExchangeConfiguration()
	{
	}

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

	@JsonProperty("test")
	public TestSettings getTestSettings()
	{
		return testSettings;
	}

	@JsonProperty("test")
	public void setTestSettings(TestSettings testSettings)
	{
		this.testSettings = testSettings;
	}
	
	

	/**
	 * Retrieve the available settings for the upload to the EMMA database
	 * @return an instance of {@link EmmaDatabaseSettings}
	 */
	@JsonProperty("emmaDatabase")
	public EmmaDatabaseSettings getEmmaDatabaseSettings() {
		return emmaDatabaseSettings;
	}

	
	/**
	 * Set the available settings for the upload to the EMMA database
	 * @param emmaDatabaseSettings instance of {@link EmmaDatabaseSettings}
	 */
	@JsonProperty("emmaDatabase")
	public void setEmmaDatabaseSettings(EmmaDatabaseSettings emmaDatabaseSettings) {
		this.emmaDatabaseSettings = emmaDatabaseSettings;
	}

	public FileStorageSettings getFileStorageSettings()
	{
		return fileStorageSettings;
	}

	public S3ProxySettings getS3Settings()
	{
		return s3Settings;
	}

	public EmailSettings getEmailsettings()
	{
		return emailsettings;
	}

	public DataSourceFactory getDataSourceFactory()
	{
		return database;

	}

	/**
	 * Extra configuration details required after standard Dropwizard config.  
	 */
	public void additionalConfig()
	{
		fileStorageSettings = new FileStorageSettings();
		fileStorageSettings.setRedisSettings(getEnvVar(ENV_VAR_REDIS_HOST), Integer.parseInt(getEnvVar(ENV_VAR_REDIS_PORT)));

		// Non-mandatory for local environment
		if (!isLocalEnvironment(miscSettings.getEnvironment()))
		{
			String s3Type = getEnvVar(ENV_VAR_DR_S3_TYPE);
			String s3Host = getEnvVar(ENV_VAR_DR_S3_HOST);
			int s3Port = Integer.parseInt(getEnvVar(ENV_VAR_DR_S3_PORT));
			s3Settings = new S3ProxySettings(s3Type, s3Host, s3Port);
		}

		String monProHost = getEnvVar(ENV_VAR_MONITOR_PRO_HOST);
		int monProPort = Integer.parseInt(getEnvVar(ENV_VAR_MONITOR_PRO_PORT));
		String monProEmailTo = getEnvVar(ENV_VAR_MONITOR_EMAIL_TO);
		String monProEmailFrom = getEnvVar(ENV_VAR_MONITOR_EMAIL_FROM);
		String monProTLS = getEnvVar(ENV_VAR_MONITOR_TLS);
		String monProBodyMessage = getEnvVar(ENV_VAR_MONITOR_BODY_MESSAGE);

		emailsettings = new EmailSettings(monProHost, monProPort, monProEmailTo, monProEmailFrom, monProTLS, monProBodyMessage);

		database = new DataSourceFactory();
		database.setUrl(getEnvVar(ENV_VAR_DATABASE_URL));
		database.setUser(getEnvVar(ENV_VAR_DATABASE_USER));
		database.setPassword(getEnvVar(ENV_VAR_DATABASE_PASSWORD));
		database.setDriverClass(getEnvVar(ENV_VAR_DATABASE_DRIVER_CLASS));
	}
}
