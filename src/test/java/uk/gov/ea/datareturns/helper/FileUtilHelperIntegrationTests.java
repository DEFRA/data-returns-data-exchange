package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// TODO implement IOException tests

/**
 * File Utility Helper class integration tests
 */
public class FileUtilHelperIntegrationTests
{
	private final static String TEST_ROOT_DIRECTORY = "test_root_directory";
	private final static String TEST_DIRECTORY = "test_directory";
	private final static String TEST_FILE_NAME = "test_file";
	private final static String TEST_CSV_EXT = "csv";
	private final static String TEST_FILE_CONTENTS = "up the City!";

	@Before
	public void setup() throws IOException
	{
		FileUtils.forceMkdir(new File(TEST_ROOT_DIRECTORY));
	}

	@After
	public void cleanUp() throws IOException
	{
		FileUtils.deleteDirectory(new File(TEST_ROOT_DIRECTORY));
	}

	@Test
	public void testCreateDirectory() throws IOException
	{
		String fullDir = getTestFullDirectory();

		// Make sure directory does not exist to start with
		File dir = new File(fullDir);
		assertThat(dir.exists()).isFalse();

		FileUtilsHelper.createDirectory(fullDir);

		assertThat(dir.exists()).isTrue();
	}

	@Test
	public void testDeleteDirectory() throws IOException
	{
		String fullDir = getTestFullDirectory();

		// Make sure directory exists to start with
		FileUtils.forceMkdir(new File(fullDir));
		File dir = new File(fullDir);
		assertThat(dir.exists()).isTrue();

		FileUtilsHelper.deleteDirectory(fullDir);

		assertThat(dir.exists()).isFalse();
	}

	@Test
	public void testDirectoryExists() throws IOException
	{
		String fullDir = getTestFullDirectory();

		FileUtils.forceMkdir(new File(fullDir));

		assertThat(FileUtilsHelper.fileOrDirectoryExists(fullDir)).isTrue();
	}

	@Test
	public void testDirectoryDoesNotExist() throws IOException
	{
		String dirExists = getTestFullDirectory();

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		String missingDir = dirExists + File.separator + "missing_dir";
		assertThat(FileUtilsHelper.fileOrDirectoryExists(missingDir)).isFalse();
	}

	@Test
	public void testFileExists() throws IOException
	{
		String fullDir = getTestFullDirectory();
		String fullFile = getTestFullCSVFilename();

		// Create file
		FileUtils.forceMkdir(new File(fullDir));
		createTestFile();

		assertThat(FileUtilsHelper.fileOrDirectoryExists(fullFile)).isTrue();
	}

	@Test
	public void testFileDoesNotExist() throws IOException
	{
		String dirExists = getTestFullDirectory();

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		String missingFile = getTestFullCSVFilename();
		assertThat(FileUtilsHelper.fileOrDirectoryExists(missingFile)).isFalse();
	}

	@Test
	public void testSaveInputStreamToFile() throws IOException
	{
		String dirExists = getTestFullDirectory();
		String fullFile = getTestFullCSVFilename();

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		// Make sure file doesn't exist to start with
		File f = new File(fullFile);
		assertThat(f.exists()).isFalse();

		FileUtilsHelper.saveFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), fullFile);

		assertThat(f.exists()).isTrue();
	}

	@Test
	public void testSaveStringToFile() throws IOException
	{
		String dirExists = getTestFullDirectory();
		String fullFile = getTestFullCSVFilename();

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		// Make sure file doesn't exist to start with
		File f = new File(fullFile);
		assertThat(f.exists()).isFalse();

		FileUtilsHelper.saveFile(TEST_FILE_CONTENTS, fullFile);

		assertThat(f.exists()).isTrue();
	}

	@Test
	public void testDeleteFile() throws IOException
	{
		String dirExists = getTestFullDirectory();
		String fullFile = getTestFullCSVFilename();

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		// Create file and check it exists
		File f = new File(fullFile);
		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), new File(fullFile));
		assertThat(f.exists()).isTrue();

		FileUtilsHelper.deleteFile(fullFile);

		assertThat(f.exists()).isFalse();
	}

	@Test
	public void testLoadFileAsString() throws IOException
	{
		String dirExists = getTestFullDirectory();
		String fullFile = getTestFullCSVFilename();
		String expected = TEST_FILE_CONTENTS;

		// Make sure directory exists to start with
		File dir = new File(dirExists);
		FileUtils.forceMkdir(new File(dirExists));
		assertThat(dir.exists()).isTrue();

		// Create file and check it exists
		File f = new File(fullFile);
		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), new File(fullFile));
		assertThat(f.exists()).isTrue();

		String actual = FileUtilsHelper.loadFileAsString(fullFile);

		assertThat(expected).isEqualTo(actual);
	}

	// Helper methods

	private static String getTestCSVFileName()
	{
		return TEST_FILE_NAME + "." + TEST_CSV_EXT;
	}

	private static String getTestRootDirectory()
	{
		return TEST_ROOT_DIRECTORY;
	}

	private static String getTestDirectory()
	{
		return TEST_DIRECTORY;
	}

	private static String getTestFullDirectory()
	{
		return getTestRootDirectory() + File.separator + getTestDirectory();
	}

	private static String getTestFullCSVFilename()
	{
		return getTestFullDirectory() + File.separator + getTestCSVFileName();
	}

	public void createTestFile() throws IOException
	{
		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), new File(getTestFullCSVFilename()));
	}
}
