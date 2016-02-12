package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.ApplicationException.PERMIT_NOT_FOUND;

import org.junit.Test;

public class ApplicationExceptionTests
{
	@Test
	public void testObjectInstantiated()
	{
		assertThat(ApplicationException.values().length).isGreaterThan(0);
		assertThat(ApplicationException.valueOf("PERMIT_NOT_FOUND").getAppStatusCode()).isEqualTo(704);
	}

	/**
	 * Check app status code and reason
	 */
	@Test
	public void testSuccess()
	{
		assertThat(PERMIT_NOT_FOUND.getAppStatusCode()).isEqualTo(704);
		assertThat(PERMIT_NOT_FOUND.getReason()).isEqualTo("Permit not found");
	}
}
