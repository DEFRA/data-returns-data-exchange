package uk.gov.ea.datareturns.type;

public enum EnvironmentType
{
	LOCAL(1, "local"), 
	DEV(2, "dev"), 
	TEST(3, "test"), 
	PRE_PROD(4, "preprod"), 
	PROD(5, "prod"); 

	private int envCode;
	private String environment;

	EnvironmentType(int envCode, String environment)
	{
		this.envCode = envCode;
		this.environment = environment;
	}

	public int getEnvCode()
	{
		return envCode;
	}

	public String getEnvironment()
	{
		return environment;
	}
}
