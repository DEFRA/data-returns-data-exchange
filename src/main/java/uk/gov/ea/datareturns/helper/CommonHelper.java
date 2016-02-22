package uk.gov.ea.datareturns.helper;

import static uk.gov.ea.datareturns.type.EnvironmentType.LOCAL;
import uk.gov.ea.datareturns.exception.application.DREnvironmentException;

public class CommonHelper
{
	public final static String ENV_LOCAL = "local";

	public static boolean isLocalEnvironment(String environment)
	{
		return (LOCAL.getEnvironment().equalsIgnoreCase(environment));
	}

	public static String getEnvVar(String envVarName)
	{
		String envVarVal = System.getenv(envVarName);
System.out.println(envVarName + " contains '" + envVarVal + "'");
		if (envVarVal == null)
		{
			throw new DREnvironmentException("Environment variable '" + envVarName + "' not found");
		}

		return envVarVal;
	}
}
