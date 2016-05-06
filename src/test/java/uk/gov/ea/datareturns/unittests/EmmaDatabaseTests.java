package uk.gov.ea.datareturns.unittests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.EmmaDatabase;

public class EmmaDatabaseTests {

	@Test
	public void testUpperDBNameFromNumericPermitNo() {
		final EmmaDatabase expected = EmmaDatabase.UPPER_NUMERIC;

		assertThat(EmmaDatabase.forUniqueId("70000")).isEqualTo(expected);
		assertThat(EmmaDatabase.forUniqueId("969001")).isEqualTo(expected);
	}

	@Test
	public void testLowerDBNameFromAlphaNumericPermitNo() {
		final EmmaDatabase expected = EmmaDatabase.LOWER_ALPHANUMERIC;

		assertThat(EmmaDatabase.forUniqueId("aa123")).isEqualTo(expected);
		assertThat(EmmaDatabase.forUniqueId("gZ123")).isEqualTo(expected);
	}

	@Test
	public void testUpperDBNameFromAlphaNumericPermitNo() {
		final EmmaDatabase expected = EmmaDatabase.UPPER_ALPHANUMERIC;

		assertThat(EmmaDatabase.forUniqueId("Ha123")).isEqualTo(expected);
		assertThat(EmmaDatabase.forUniqueId("zZ123")).isEqualTo(expected);
	}

	@Test
	public void testUndeterminableDBName() {
		assertThat(EmmaDatabase.forUniqueId(";a123") == null);
	}
}
