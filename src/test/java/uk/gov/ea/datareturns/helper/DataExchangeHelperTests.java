package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.isAlphaNumericPermitNo;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.isNumericPermitNo;

import org.junit.Test;

import uk.gov.ea.datareturns.domain.dataexchange.EmmaDatabase;
import uk.gov.ea.datareturns.exception.application.DRPermitNotRecognisedException;

public class DataExchangeHelperTests
{
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
		EmmaDatabase expected = EmmaDatabase.LOWER_NUMERIC;

		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("10000")).isEqualTo(expected);
		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("69999")).isEqualTo(expected);
	}

	@Test
	public void testUpperDBNameFromNumericPermitNo()
	{
		EmmaDatabase expected = EmmaDatabase.UPPER_NUMERIC;

		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("70000")).isEqualTo(expected);
		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("969001")).isEqualTo(expected);
	}

	@Test
	public void testLowerDBNameFromAlphaNumericPermitNo()
	{
		EmmaDatabase expected = EmmaDatabase.LOWER_ALPHANUMERIC;

		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("aa123")).isEqualTo(expected);
		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("gZ123")).isEqualTo(expected);
	}

	@Test
	public void testUpperDBNameFromAlphaNumericPermitNo()
	{
		EmmaDatabase expected = EmmaDatabase.UPPER_ALPHANUMERIC;

		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("Ha123")).isEqualTo(expected);
		assertThat(DataExchangeHelper.getDatabaseTypeFromPermitNo("zZ123")).isEqualTo(expected);
	}

	@Test
	public void testUndeterminableDBName()
	{
		try
		{
			DataExchangeHelper.getDatabaseTypeFromPermitNo(";a123");
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(DRPermitNotRecognisedException.class);
		}
	}
}
