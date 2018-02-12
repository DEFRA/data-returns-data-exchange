package uk.gov.defra.datareturns.tests.unit;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.defra.datareturns.util.UTF8Checker;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Interim solution for UTF-8 character issue while the lists are being maintained in Excel..
 */
public class DataQualityTests {
    // Expected entry count checked by SGD 2016-09-23
    private static final int VALID_UTF8_CHAR_COUNT = 38;

    @Test
    public void testUTF8Characters() throws URISyntaxException {
        final File dbDataDir = new File(DataQualityTests.class.getResource("/db/data/").toURI());
        final File[] files = dbDataDir.listFiles();

        int count = 0;
        if (files != null) {
            for (final File f : files) {
                if (f.isFile()) {
                    count += UTF8Checker.checkFile(f, UTF8Checker.MAX_ASCII_EXTENDED).size();
                }
            }
        }
        if (VALID_UTF8_CHAR_COUNT != count) {
            Assert.fail("Mismatch between expected UTF-8 character count ("
                    + VALID_UTF8_CHAR_COUNT + ") in controlled lists and actual count ("
                    + count + "), the lists must be checked!");
        }
    }
}
