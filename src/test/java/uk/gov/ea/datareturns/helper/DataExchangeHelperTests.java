package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DataExchangeHelperTests
{
	@Test
	public void testObjectCreation()
	{
		@SuppressWarnings("unused")
		DataExchangeHelper helper = new DataExchangeHelper();
	}


//	@Test
//	// TODO current test conditions assume UUID generated string is acceptable - use regex?
//	public void testFileKeyGeneration()
//	{
//		final String result = generateFileKey();
//
//		assertThat(result.length()).isEqualTo(36);
//		assertThat(result.substring(8, 9)).isEqualTo("-");
//		assertThat(result.substring(13, 14)).isEqualTo("-");
//		assertThat(result.substring(18, 19)).isEqualTo("-");
//		assertThat(result.substring(23, 24)).isEqualTo("-");
//	}
}
