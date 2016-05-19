package uk.gov.ea.datareturns.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.exception.application.ProcessingException;
import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageProvider;
import uk.gov.ea.datareturns.storage.StorageProvider.StoredFile;

/**
 * Processor Integration Tests
 *
 * The purpose of tests in this class is to check that for a given input file the output is transformed as expected.
 *
 * For example, we accept boolean values true, false, yes, no, 1, 0 (or any case variation of this) but should always output
 * boolean values using the standard true/false notation
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("IntegrationTests")
@DirtiesContext
public class ProcessorIntegrationTests {
	public final static String IO_TESTS_FOLDER = "/testfiles/iotests/";

	public final static String BOOLEAN_TESTS = "boolean-values.csv";

	@Inject
	private ApplicationContext context;

	@Inject
	private StorageProvider storage;

	/**
	 * Tests boolean values are converted as necessary.
	 */
	@Test
	public void testBooleanValues() {
		final Collection<File> outputFiles = getOutputFiles(getTestFileStream(BOOLEAN_TESTS));
		Assertions.assertThat(outputFiles.size()).isEqualTo(1);
		final String[] expected = { "true", "false", "true", "false", "true", "false", "true", "false" };
		verifyCSVValues(outputFiles.iterator().next(), DataReturnsHeaders.TEXT_VALUE, expected);
	}

	/**
	 * Retrieve the set of output files which are created by the processors for the given {@link InputStream}
	 *
	 * @param inputStream the {@link InputStream} containing DEP compliant
	 * @return
	 */
	private Collection<File> getOutputFiles(final InputStream inputStream) {
		final FileUploadProcessor processor = this.context.getBean(FileUploadProcessor.class);

		processor.setClientFilename("ProcessorIntegrationTests.csv");
		processor.setInputStream(inputStream);

		DataExchangeResult result = null;
		try {
			result = processor.process();
		} catch (final ProcessingException e) {
			Assertions.fail("Processor exception thrown", e);
		}
		Assertions.assertThat(result.getParseResult().getMappings().isEmpty()).isFalse();
		Assertions.assertThat(result.getUploadResult().getFileKey()).isNotEmpty();

		final String fileKey = result.getUploadResult().getFileKey();
		try {
			final StoredFile storedFile = this.storage.retrieveTemporaryData(fileKey);
			final File workingFolder = org.assertj.core.util.Files.temporaryFolder();
			final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(workingFolder, storedFile.getFile());
			return zipModel.getOutputFiles();
		} catch (StorageException | IOException e) {
			throw new AssertionError("Unable to retrieve stored file.", e);
		}
	}

	/**
	 * For a given output CSV file, check that the values in the specified column match those that are expected
	 *
	 * @param csvFile the CSV file to parse
	 * @param columnName the column header of the data to be checked
	 * @param expectedValues the expected values to be found in the column (in document order)
	 */
	private static void verifyCSVValues(final File csvFile, final String columnName, final String[] expectedValues) {
		final ColumnProcessor rowProcessor = new ColumnProcessor();
		final CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setRowProcessor(rowProcessor);

		final CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(csvFile);
			final List<String> columnData = rowProcessor.getColumn(columnName);
			Assertions.assertThat(expectedValues.length).isEqualTo(columnData.size());

			for (int i = 0; i < columnData.size(); i++) {
				Assertions.assertThat(columnData.get(i))
						.as("Mismatched value on row " + (i + 1))
						.isEqualTo(expectedValues[i]);
			}
		} catch (final TextParsingException e) {
			throw new AssertionError("Unable to parse output CSV file.", e);
		}
	}

	/**
	 * Return an InputStream for a given test file
	 *
	 * @param testFile the name of the test file to resolve
	 * @return an {@link InputStream} from the test file
	 */
	private static InputStream getTestFileStream(final String testFile) {
		return ProcessorIntegrationTests.class.getResourceAsStream(IO_TESTS_FOLDER + testFile);
	}
}