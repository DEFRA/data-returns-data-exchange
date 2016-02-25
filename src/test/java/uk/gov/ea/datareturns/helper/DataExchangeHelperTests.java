package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.getDatabaseNameFromPermitNo;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.isAlphaNumericPermitNo;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.isNumericPermitNo;

import org.junit.Test;

import uk.gov.ea.datareturns.exception.application.DRInvalidPermitNoException;

public class DataExchangeHelperTests
{
	@Test
	public void coverage()
	{
		@SuppressWarnings("unused")
		DataExchangeHelper helper = new DataExchangeHelper();
	}

	@Test
	public void testInValidNumericPermitNo()
	{
		assertThat(isNumericPermitNo("1234")).isFalse();
		assertThat(isNumericPermitNo("1234567")).isFalse();
		assertThat(isNumericPermitNo("12A45")).isFalse();
		assertThat(isNumericPermitNo("1234B6")).isFalse();
	}

	@Test
	public void testValidNumericPermitNo()
	{
		assertThat(isNumericPermitNo("12345")).isTrue();
		assertThat(isNumericPermitNo("123456")).isTrue();
	}

	@Test
	public void testInValidAlphaNumericPermitNo()
	{
		assertThat(isAlphaNumericPermitNo("1234")).isFalse();
		assertThat(isAlphaNumericPermitNo("$7")).isFalse();
		assertThat(isAlphaNumericPermitNo("(A")).isFalse();
	}

	@Test
	public void testValidAlphaNumericPermitNo()
	{
		assertThat(isAlphaNumericPermitNo("AB")).isTrue();
		assertThat(isAlphaNumericPermitNo("AB12345")).isTrue();
		assertThat(isAlphaNumericPermitNo("aB12345")).isTrue();
		assertThat(isAlphaNumericPermitNo("ab12345")).isTrue();
	}

	@Test
	public void testLowerDBNameFromNumericPermitNo()
	{
		String expected = "EA_LP_10000_TO_69000_LIST";

		assertThat(getDatabaseNameFromPermitNo("10000")).isEqualTo(expected);
		assertThat(getDatabaseNameFromPermitNo("69999")).isEqualTo(expected);
	}

	@Test
	public void testUpperDBNameFromNumericPermitNo()
	{
		String expected = "EA_LP_70000_ABOVE_LIST";

		assertThat(getDatabaseNameFromPermitNo("70000")).isEqualTo(expected);
		assertThat(getDatabaseNameFromPermitNo("969001")).isEqualTo(expected);
	}

	@Test
	public void testLowerDBNameFromAlphaNumericPermitNo()
	{
		String expected = "EA_LP_AA_TO_GZ_LIST";

		assertThat(getDatabaseNameFromPermitNo("aa123")).isEqualTo(expected);
		assertThat(getDatabaseNameFromPermitNo("gZ123")).isEqualTo(expected);
	}

	@Test
	public void testUpperDBNameFromAlphaNumericPermitNo()
	{
		String expected = "EA_LP_HA_TO_ZZ_LIST";

		assertThat(getDatabaseNameFromPermitNo("Ha123")).isEqualTo(expected);
		assertThat(getDatabaseNameFromPermitNo("zZ123")).isEqualTo(expected);
	}

	@Test
	public void testUndeterminableDBName()
	{
		try
		{
			getDatabaseNameFromPermitNo(";a123");
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(DRInvalidPermitNoException.class);
		}
	}
}
