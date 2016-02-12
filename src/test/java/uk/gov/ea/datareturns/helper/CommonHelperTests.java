package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.CommonHelper.isLocalEnvironment;

import org.junit.Test;

public class CommonHelperTests
{
	@Test
	public void coverage()
	{
		@SuppressWarnings("unused")
		CommonHelper helper = new CommonHelper();
	}

	@Test
	public void testIsLocalEnvironment()
	{
		assertThat(isLocalEnvironment("local")).isTrue();
		assertThat(isLocalEnvironment("LoCal")).isTrue();
		assertThat(isLocalEnvironment("LOCAL")).isTrue();
	}

	@Test
	public void testIsNotLocalEnvironment()
	{
		assertThat(isLocalEnvironment("dev")).isFalse();
		assertThat(isLocalEnvironment("test")).isFalse();
		assertThat(isLocalEnvironment("preprod")).isFalse();
		assertThat(isLocalEnvironment("prod")).isFalse();
		assertThat(isLocalEnvironment("anthing")).isFalse();
	}
}
