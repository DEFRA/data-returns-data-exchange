package uk.gov.ea.datareturns.tests.unittests;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.ea.datareturns.util.UTF8Checker;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Interim solution for UTF-8 character issue while the lists are being maintained in Excel..
 */
public class DataQualityTests {
    // Expected entry count checked by SGD 2016-09-23
    private static final int VALID_UTF8_CHAR_COUNT = 30;

    @Test
    public void testUTF8Characters() throws URISyntaxException {
        File dbDataDir = new File(DataQualityTests.class.getResource("/db/data/").toURI());
        int count = 0;
        for (File f : dbDataDir.listFiles()) {
            // Have added a sub-directory on this path for the
            // Static permit data - skip past directories
            if (!f.isDirectory()) {
                count += UTF8Checker.checkFile(f, UTF8Checker.MAX_ASCII_EXTENDED).size();
            }
        }
        if (VALID_UTF8_CHAR_COUNT != count) {
            Assert.fail("Mismatch between expected UTF-8 character count in controlled lists and actual count, the lists must be checked!");
        }
    }
}
