package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.fileContainsMinRows;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.saveReturnsFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
	public void testSaveReturnsFileSuccess()
	{
		InputStream inputStream = new ByteArrayInputStream("any string".getBytes(StandardCharsets.UTF_8));

		assertThat(saveReturnsFile(inputStream, makeFullPath("./uploaded", FILE_CSV_SUCCESS))).isEqualTo(true);
	}

	@Test
	public void testSaveReturnsFailure()
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
	public void testSaveReturnsReadFailure()
	{
		InputStream mockIS = mock(InputStream.class);
		InputStreamReader isReader = Mockito.mock(InputStreamReader.class);
		BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

		try
		{
			PowerMockito.whenNew(InputStreamReader.class).withArguments(mockIS).thenReturn(isReader);
			PowerMockito.whenNew(BufferedReader.class).withArguments(isReader).thenReturn(bufferedReader);
			PowerMockito.when(bufferedReader.readLine()).thenReturn("any old junk").thenThrow(new IOException("testSaveReturnsReadFailure"));

			boolean ret = saveReturnsFile(mockIS, makeFullPath("./uploaded", FILE_CSV_SUCCESS));
			assertThat(ret).isFalse();
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(FileReadException.class);
		}
	}

	@Test
	public void testMinimalDataFileNotFound()
	{
		try
		{
			fileContainsMinRows(makeFullPath("any_directory", FILE_CSV_SUCCESS), 2);
		} catch (Exception e)
		{
			assertThat(e).isInstanceOf(FileUnlocatableException.class);
		}
	}

// TODO test for IOException when fileContainsMinRows() refactored
//	@Test
//	public void testMinimalDataFileReadFailure()
//	{
//	}

	@Test
	public void testLessThanMinimumData()
	{
		assertThat(fileContainsMinRows(makeFullPath(TEST_FILES_PATH, FILE_CSV_INSUFFICIENT_DATA), 2)).isFalse();
	}

	@Test
	public void testSufficientData()
	{
		assertThat(fileContainsMinRows(makeFullPath(TEST_FILES_PATH, FILE_CSV_SUCCESS), 2)).isTrue();
	}
}
