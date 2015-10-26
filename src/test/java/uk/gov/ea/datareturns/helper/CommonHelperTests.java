package uk.gov.ea.datareturns.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.CommonHelper.getFileType;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;

import org.junit.Test;

public class CommonHelperTests
{
	final private String TEST_PATH = "/this/is/a/test/folder";
	final private String TEST_FILE = "test_file.csv";

	@Test
	public void testMakeFullFilePath()
	{
		final String EXPECTED = "/this/is/a/test/folder/test_file.csv";

		final String result = makeFullPath(TEST_PATH, TEST_FILE);
		assertThat(result).isEqualTo(EXPECTED);
	}

	/**
	 * Must always return lower case file extension
	 */
	@Test
	public void testExtractFileType()
	{
		final String EXPECTED_FILE_TYPE = "csv";

		String result = getFileType(makeFullPath(TEST_PATH.toUpperCase(), TEST_FILE.toUpperCase()));
		assertThat(result).isEqualTo(EXPECTED_FILE_TYPE);
	}
}
