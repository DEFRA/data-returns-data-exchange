/**
 *
 */
package uk.gov.ea.datareturns.tests.unit;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.ea.datareturns.config.storage.LocalStorageConfiguration;
import uk.gov.ea.datareturns.domain.storage.StorageException;
import uk.gov.ea.datareturns.domain.storage.StorageProvider.StoredFile;
import uk.gov.ea.datareturns.domain.storage.local.LocalStorageProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

/**
 * @author Sam Gardner-Dell
 *
 */
public class LocalStorageProviderTests {
    private static File tempDir;

    private static File auditDir;

    private static File testFile;

    @BeforeClass
    public static void beforeClass() throws IOException, URISyntaxException {
        tempDir = Files.createTempDirectory("testTempLocalStore").toFile();
        auditDir = Files.createTempDirectory("testAuditLocalStore").toFile();

        final URL testFileURL = LocalStorageProviderTests.class.getResource("/testfiles/success.csv");
        testFile = new File(testFileURL.toURI());

        FileUtils.forceMkdir(tempDir);
        FileUtils.forceMkdir(auditDir);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.forceDelete(tempDir);
        FileUtils.forceDelete(auditDir);
    }

    @Test
    public void testFullCycle() throws StorageException, IOException {
        final LocalStorageConfiguration settings = new LocalStorageConfiguration();
        settings.setTemporaryFolder(tempDir);
        settings.setPersistentFolder(auditDir);

        final LocalStorageProvider provider = new LocalStorageProvider(settings);
        final String key = provider.storeTemporaryData(testFile);
        final File tempFile = new File(tempDir, key);
        Assert.assertTrue(tempFile.exists());

        final StoredFile storedFile = provider.retrieveTemporaryData(key);
        Assert.assertTrue(storedFile.getFile().exists());
        Assert.assertTrue(FileUtils.contentEquals(tempFile, storedFile.getFile()));

        final String auditKey = provider.moveToAuditStore(key, null);
        Assert.assertFalse(tempFile.exists());
        Assert.assertTrue(new File(auditDir, auditKey).exists());
    }
}
