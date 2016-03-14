package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

// TODO IOException tests need implementing

/**
 * File Utility Helper class unit/integeration tests
 * @author adrianharrison
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsHelperTests
{
	private final static String TEST_ROOT_DIRECTORY = "test_root_directory";
	private final static String TEST_DIRECTORY = "test_directory";
	private final static String TEST_FILE_NAME = "test_file";
	private final static String TEST_CSV_EXT = "csv";
	private final static String TEST_XML_EXT = "xml";
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
	public void testMakeFullPath()
	{
		String expected = getTestFullCSVFilename();
		String actual = FileUtilsHelper.makeFullPath(getTestFullDirectory(), getTestCSVFileName());

		assertThat(expected).isEqualTo(actual);
	}

	@Test
	public void testDeleteDirectory() throws IOException
	{
		String fullDir = getTestFullDirectory();

		// Make sure directory exists to start with
		FileUtils.forceMkdir(new File(fullDir));
		File dir = new File(fullDir);
		assertThat(dir.exists()).isTrue();

		FileUtils.deleteDirectory(new File(fullDir));

		assertThat(dir.exists()).isFalse();
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

		FileUtilsHelper.saveFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), f);

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

	// Helper methods

	private static String getTestCSVFileName()
	{
		return TEST_FILE_NAME + "." + TEST_CSV_EXT;
	}

	private static String getTestXMLFileName()
	{
		return TEST_FILE_NAME + "." + TEST_XML_EXT;
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

	@SuppressWarnings("unused")
	private static String getTestFullXMLFilename()
	{
		return getTestFullDirectory() + File.separator + getTestXMLFileName();
	}

	public void createTestFile() throws IOException
	{
		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(TEST_FILE_CONTENTS.getBytes()), new File(getTestFullCSVFilename()));
	}
}
