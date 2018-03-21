package uk.gov.defra.datareturns.tests.unit;

import org.junit.Test;
import uk.gov.defra.datareturns.util.TextUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sam on 21/11/16.
 */
public class TextUtilsTest {
    @Test
    public void testCharacterSubstitution() {
        final StringBuilder inputString = new StringBuilder();
        final StringBuilder expectedResult = new StringBuilder();
        for (final Map.Entry<Character, Character> entry : TextUtils.CharacterSubstitution.SUBSTITUTIONS.entrySet()) {
            if (!TextUtils.CharacterSubstitution.isSpace(entry.getKey())) {
                inputString.append(entry.getKey());
                expectedResult.append(entry.getValue());
            }
        }
        assertThat(TextUtils.normalize(inputString.toString())).isEqualTo(expectedResult.toString().trim());
    }

    @Test
    public void testWhitespaceCollapse() {
        final String inputString = "   \r\u200bThe  \u3000\u2005  cat  \u2007  sat  \u2009  on\u0009\n    the "
                + "\u00a0\u2028\u202f\u2060\ufeffmat\n\r\r\n\t   ";
        final String expectedResult = "The cat sat on the mat";
        assertThat(TextUtils.normalize(inputString)).isEqualTo(expectedResult);
    }
}
