package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.EnvironmentType.DEV;
import static uk.gov.ea.datareturns.type.EnvironmentType.LOCAL;
import static uk.gov.ea.datareturns.type.EnvironmentType.PRE_PROD;
import static uk.gov.ea.datareturns.type.EnvironmentType.PROD;
import static uk.gov.ea.datareturns.type.EnvironmentType.TEST;

import org.junit.Test;

/**
 * Tests for Environment types.
 * Environment descriptions included for completeness.
 */
public class EnvironmentTypeTests
{
	@Test
	public void coverage()
	{
		assertThat(EnvironmentType.values().length).isGreaterThan(0);
		assertThat(EnvironmentType.valueOf("LOCAL").getEnvCode()).isEqualTo(1);
	}

	@Test
	public void testFileTypes()
	{
		assertThat(LOCAL.getEnvCode()).isEqualTo(1);
		assertThat(DEV.getEnvCode()).isEqualTo(2);
		assertThat(TEST.getEnvCode()).isEqualTo(3);
		assertThat(PRE_PROD.getEnvCode()).isEqualTo(4);
		assertThat(PROD.getEnvCode()).isEqualTo(5);
	}

	@Test
	public void testDescriptions()
	{
		assertThat(LOCAL.getEnvironment()).isEqualTo("local");
		assertThat(DEV.getEnvironment()).isEqualTo("dev");
		assertThat(TEST.getEnvironment()).isEqualTo("test");
		assertThat(PRE_PROD.getEnvironment()).isEqualTo("preprod");
		assertThat(PROD.getEnvironment()).isEqualTo("prod");
	}
}
