package uk.gov.ea.datareturns.helper;

import static uk.gov.ea.datareturns.type.EnvironmentType.LOCAL;
import uk.gov.ea.datareturns.exception.application.DREnvironmentException;

public abstract class CommonHelper
{
	public final static String ENV_LOCAL = "local";

	/**
	 * Determines if app running in local environment 
	 * @param environment
	 * @return
	 */
	// TODO probably belongs DataExchangeConfiguration
	public static boolean isLocalEnvironment(String environment)
	{
		return (LOCAL.getEnvironment().equalsIgnoreCase(environment));
	}

	/**
	 * Read an environment variable - assumes variable is mandatory
	 * @param envVarName
	 * @return
	 */
	public static String getEnvVar(String envVarName)
	{
		String envVarVal = System.getenv(envVarName);

		if (envVarVal == null)
		{
			throw new DREnvironmentException("Environment variable '" + envVarName + "' not found");
		}

		return envVarVal;
	}
}
