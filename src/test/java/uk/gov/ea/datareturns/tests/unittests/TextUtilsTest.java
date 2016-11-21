package uk.gov.ea.datareturns.tests.unittests;

import org.junit.Test;
import uk.gov.ea.datareturns.util.TextUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sam on 21/11/16.
 */
public class TextUtilsTest {
    @Test
    public void testCharacterSubstitution() {
        String inputString = "";
        String expectedResult = "";
        for (Map.Entry<Character, Character> entry : TextUtils.CharacterSubstitution.SUBSTITUTIONS.entrySet()) {
            inputString += entry.getKey();
            expectedResult += entry.getValue();
        }
        assertThat(TextUtils.normalize(inputString)).isEqualTo(expectedResult);
    }

    @Test
    public void testWhitespaceCollapse() {
        final String inputString = "   \r\u200bThe  \u3000\u2005  cat  \u2007  sat  \u2009  on\u0009\n    the \u00a0\u2028\u202f\u2060\ufeffmat\n\r\r\n\t   ";
        final String expectedResult = " The cat sat on the mat ";
        assertThat(TextUtils.normalize(inputString)).isEqualTo(expectedResult);
    }
}