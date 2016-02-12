package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.AppStatusCodeType.APP_STATUS_FAILED_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.AppStatusCodeType.APP_STATUS_SUCCESS;

import org.junit.Test;

/**
 * Tests for App Status Code types.
 * Reasons included for completeness.
 */
public class AppStatusCodeTests
{
	@Test
	public void coverage()
	{
		assertThat(AppStatusCodeType.values().length).isGreaterThan(0);
		assertThat(AppStatusCodeType.valueOf("APP_STATUS_SUCCESS").getAppStatusCode()).isEqualTo(800);
	}

	@Test
	public void testAppStatusCodes()
	{
		assertThat(APP_STATUS_SUCCESS.getAppStatusCode()).isEqualTo(800);
		assertThat(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode()).isEqualTo(801);
	}

	@Test
	public void testReasons()
	{
		assertThat(APP_STATUS_SUCCESS.getReason()).isEqualTo("Success");
		assertThat(APP_STATUS_FAILED_WITH_ERRORS.getReason()).isEqualTo("Failed with errors");
	}
}
