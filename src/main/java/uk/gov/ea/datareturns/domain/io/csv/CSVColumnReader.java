/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * Efficiently retrieves data from a single column in a CSV file.
 *
 * @author Sam Gardner-Dell
 */
public final class CSVColumnReader {

	/** Private Utility class constructor */
	private CSVColumnReader() {
	}

	/**
	 * Read the values from a single column of a CSV file
	 *
	 * @param csvFile the CSV file from which to parse values
	 * @param columnName the name (column heading) of the particular column to be read
	 * @return a {@link List} of {@link String}s containing the data read from the specified column
	 * @throws TextParsingException if an problem occurs reading data from the CSV file
	 */
	public static List<String> readColumn(final File csvFile, final String columnName) throws TextParsingException {
		List<String> columnData;

		final ColumnProcessor rowProcessor = new ColumnProcessor();
		final CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setRowProcessor(rowProcessor);
		// Only read the column that we're after!
		parserSettings.selectFields(columnName);

		final CsvParser parser = new CsvParser(parserSettings);
		parser.parse(csvFile);

		try {
			columnData = rowProcessor.getColumn(columnName);
		} catch (final NullPointerException e) {
			// NullPointerException thrown by univocity parser if attempting to parse an empty or header-only file (how naff!)  We really
			// don't want these exceptions being thrown past this method and returning an empty List in this instance is the correct behaviour.
			columnData = Collections.emptyList();
		}
		return columnData;
	}
}