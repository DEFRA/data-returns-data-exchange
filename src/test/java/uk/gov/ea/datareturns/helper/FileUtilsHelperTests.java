package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.getFileType;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.makeFullPath;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

// TODO add all unit tests
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsHelperTests
{
	public final static String TEST_FILES_PATH = "src/test/resources/testfiles";
	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";

//	final private String TEST_PATH = "/this/is/a/test/folder";
//	final private String TEST_FILE = "test_file.csv";
	
	@Test
	public void testObjectCreation()
	{
		@SuppressWarnings("unused")
		FileUtilsHelper helper = new FileUtilsHelper();
	}

	// TODO all tests - check all and add missing
	
//	@Test
//	public void testMakeFullFilePath()
//	{
//		final String EXPECTED = "/this/is/a/test/folder/test_file.csv";
//
//		final String result = makeFullPath(TEST_PATH, TEST_FILE);
//		assertThat(result).isEqualTo(EXPECTED);
//	}
//
//	/**
//	 * Must return null
//	 */
//	@Test
//	public void testExtractFileTypeFailure()
//	{
//		@SuppressWarnings("unused")
//		final String EXPECTED_FILE_TYPE = "csv";
//
//		String result = getFileType(makeFullPath("", ""));
//		assertThat(result).isEqualTo(null);
//	}
//
//	/**
//	 * Must always return lower case file extension
//	 */
//	@Test
//	public void testExtractFileTypeSuccess()
//	{
//		final String EXPECTED_FILE_TYPE = "csv";
//
//		String result = getFileType(makeFullPath(TEST_PATH.toUpperCase(), TEST_FILE.toUpperCase()));
//		assertThat(result).isEqualTo(EXPECTED_FILE_TYPE);
//	}
//	
//	@Test
//	public void testSaveReturnsFileSuccess()
//	{
//		InputStream inputStream = new ByteArrayInputStream("any string".getBytes(StandardCharsets.UTF_8));
//
//		assertThat(saveFile(inputStream, makeFullFilePath("./uploaded", FILE_CSV_SUCCESS))).isEqualTo(true);
//	}
//
//	@Test
//	public void testSaveReturnsFailure()
//	{
//		InputStream inputStream = new ByteArrayInputStream("any string".getBytes(StandardCharsets.UTF_8));
//
//		try
//		{
//			saveFile(inputStream, makeFullFilePath("any_directory", FILE_CSV_SUCCESS));
//		} catch (Exception e)
//		{
//			assertThat(e).isInstanceOf(FileSaveException.class);
//		}
//	}
//
//	@Test
//	public void testSaveReturnsReadFailure()
//	{
//		InputStream mockIS = mock(InputStream.class);
//		InputStreamReader isReader = Mockito.mock(InputStreamReader.class);
//		BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
//
//		try
//		{
//			PowerMockito.whenNew(InputStreamReader.class).withArguments(mockIS).thenReturn(isReader);
//			PowerMockito.whenNew(BufferedReader.class).withArguments(isReader).thenReturn(bufferedReader);
//			PowerMockito.when(bufferedReader.readLine()).thenReturn("any old junk").thenThrow(new IOException("testSaveReturnsReadFailure"));
//
//			boolean ret = saveFile(mockIS, makeFullFilePath("./uploaded", FILE_CSV_SUCCESS));
//			assertThat(ret).isFalse();
//		} catch (Exception e)
//		{
//			assertThat(e).isInstanceOf(FileReadException.class);
//		}
//	}
}
