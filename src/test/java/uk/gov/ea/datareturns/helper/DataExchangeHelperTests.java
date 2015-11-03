package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.generateUniqueFileKey;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.makeSchemaName;

import org.junit.Test;

public class DataExchangeHelperTests
{
	@Test
	public void testObjectCreation()
	{
		@SuppressWarnings("unused")
		DataExchangeHelper helper = new DataExchangeHelper();
	}

	
	@Test
	public void testMakeSchemaName()
	{
		final String EXPECTED = "landfill_gas_monitoring.csvs";

		final String result = makeSchemaName("Landfill Gas Monitoring");
		assertThat(result).isEqualTo(EXPECTED);
	}

	@Test
	// TODO current test conditions assume UUID generated string is acceptable - use regex?
	public void testFileKeyGeneration()
	{
		final String result = generateUniqueFileKey();

		assertThat(result.length()).isEqualTo(36);
		assertThat(result.substring(8, 9)).isEqualTo("-");
		assertThat(result.substring(13, 14)).isEqualTo("-");
		assertThat(result.substring(18, 19)).isEqualTo("-");
		assertThat(result.substring(23, 24)).isEqualTo("-");
	}
}
