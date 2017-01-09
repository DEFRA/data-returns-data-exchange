/**
 *
 */
package uk.gov.ea.datareturns.tests.unit;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

/**
 * @author Sam Gardner-Dell
 *
 */
public class DataReturnsZipFileModelTests {
    public final static String FILE_TEST_INPUT = "success.csv";

    public final static String FILE_TEST_OUTPUT1 = "required-fields-only.csv";

    public final static String FILE_TEST_OUTPUT2 = "required-fields-missing.csv";

    private static File tempFolder;

    @BeforeClass
    public static void setup() throws IOException {
        tempFolder = Files.createTempDirectory("drtest").toFile();
    }

    @AfterClass
    public static void clean() throws IOException {
        FileUtils.deleteQuietly(tempFolder);
    }

    @Test
    public void testZipModelReadWrite() throws URISyntaxException, IOException {
        final DataReturnsZipFileModel model = new DataReturnsZipFileModel();
        model.setInputFile(getTestFile(FILE_TEST_INPUT));
        model.addOutputFile(getTestFile(FILE_TEST_OUTPUT1));
        model.addOutputFile(getTestFile(FILE_TEST_OUTPUT2));
        final File zipFile = model.toZipFile(tempFolder);
        Assert.assertTrue(zipFile.exists());

        final DataReturnsZipFileModel readerModel = DataReturnsZipFileModel.fromZipFile(tempFolder, zipFile);
        Assert.assertTrue(FileUtils.contentEquals(getTestFile(FILE_TEST_INPUT), readerModel.getInputFile()));
        Assert.assertTrue(readerModel.getOutputFiles().size() == 2);

        for (final File f : readerModel.getOutputFiles()) {
            Assert.assertTrue(FileUtils.contentEquals(getTestFile(f.getName()), f));
        }
    }

    private static File getTestFile(final String testFileName) throws URISyntaxException {
        final URL url = DataReturnsZipFileModelTests.class.getResource("/testfiles/" + testFileName);
        return new File(url.toURI());
    }
}