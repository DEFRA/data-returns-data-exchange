package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.SystemExceptionType.UNPROCESSABLE_ENTITY;

import org.junit.Test;

public class SystemExceptionTests
{
	@Test
	public void testObjectInstantiated()
	{
		assertThat(SystemExceptionType.values().length).isGreaterThan(0);
		assertThat(SystemExceptionType.valueOf("UNPROCESSABLE_ENTITY").getCode()).isEqualTo(422);
	}

	/**
	 * Check code and reason
	 */
	@Test
	public void testSuccess()
	{
		assertThat(UNPROCESSABLE_ENTITY.getCode()).isEqualTo(422);
		assertThat(UNPROCESSABLE_ENTITY.getReason()).isEqualTo("Unprocessable entity");
	}
}
