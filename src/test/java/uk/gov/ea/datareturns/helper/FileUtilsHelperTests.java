package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.fileContainsMinRows;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.saveReturnsFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

// TODO need to test IOExceptions
public class FileUtilsHelperTests
{
	public final static String TEST_FILES_PATH = "src/test/resources/testfiles";
	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";

	@Test
	public void testObjectCreation()
	{
		@SuppressWarnings("unused")
		FileUtilsHelper helper = new FileUtilsHelper();
	}

	@Test
	public void testSaveReturnsFileNotFound()
	{
		InputStream inputStream = new ByteArrayInputStream("any string".getBytes(StandardCharsets.UTF_8));
		try
		{
			saveReturnsFile(inputStream, makeFullPath("any_directory", FILE_CSV_SUCCESS));
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(FileSaveException.class);
		}
	}

	@Test
	public void testInsufficentDataNotFound()
	{
		try
		{
			fileContainsMinRows(makeFullPath("any_directory", FILE_CSV_SUCCESS), 2);
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(FileUnlocatableException.class);
		}
	}

	@Test
	public void testInsufficentData()
	{
		assertThat(fileContainsMinRows(makeFullPath(TEST_FILES_PATH, FILE_CSV_INSUFFICIENT_DATA), 2)).isFalse();
	}

	@Test
	public void testSufficentData()
	{
		assertThat(fileContainsMinRows(makeFullPath(TEST_FILES_PATH, FILE_CSV_SUCCESS), 2)).isTrue();
	}
}
