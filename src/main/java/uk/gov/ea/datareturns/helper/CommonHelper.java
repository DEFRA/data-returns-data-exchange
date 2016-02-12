package uk.gov.ea.datareturns.helper;

import static uk.gov.ea.datareturns.type.EnvironmentType.LOCAL;

public class CommonHelper
{
	public final static String ENV_LOCAL = "local";

	public static boolean isLocalEnvironment(String environment)
	{
		return (LOCAL.getEnvironment().equalsIgnoreCase(environment));
	}
}
