package uk.gov.ea.datareturns.tests.unit;

import org.junit.Test;
import uk.gov.ea.datareturns.domain.validation.model.rules.EaIdType;

import static org.assertj.core.api.Assertions.assertThat;

public class EaIdTypeTests {

    @Test
    public void testUpperDBNameFromNumericPermitNo() {
        final EaIdType expected = EaIdType.UPPER_NUMERIC;

        assertThat(EaIdType.forUniqueId("70000")).isEqualTo(expected);
        assertThat(EaIdType.forUniqueId("969001")).isEqualTo(expected);
    }

    @Test
    public void testLowerDBNameFromAlphaNumericPermitNo() {
        final EaIdType expected = EaIdType.LOWER_ALPHANUMERIC;

        assertThat(EaIdType.forUniqueId("aa123")).isEqualTo(expected);
        assertThat(EaIdType.forUniqueId("gZ123")).isEqualTo(expected);
    }

    @Test
    public void testUpperDBNameFromAlphaNumericPermitNo() {
        final EaIdType expected = EaIdType.UPPER_ALPHANUMERIC;

        assertThat(EaIdType.forUniqueId("Ha123")).isEqualTo(expected);
        assertThat(EaIdType.forUniqueId("zZ123")).isEqualTo(expected);
    }

    @Test
    public void testUndeterminableDBName() {
        assertThat(EaIdType.forUniqueId(";a123") == null);
    }
}
