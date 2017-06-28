package uk.gov.ea.datareturns.domain.io.csv;

import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.util.EncodingSupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Efficiently retrieves data from a single column in a CSV file.
 *
 * @author Sam Gardner-Dell
 */
public final class CSVColumnReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVColumnReader.class);

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
        parserSettings.setProcessor(rowProcessor);
        // Only read the column that we're after!
        parserSettings.selectFields(columnName);

        final CsvParser parser = new CsvParser(parserSettings);
        try {
            byte[] data = FileUtils.readFileToByteArray(csvFile);
            parser.parse(new ByteArrayInputStream(data), EncodingSupport.detectCharset(data));
        } catch (final Throwable e) {
            LOGGER.error("Error encountered parsing CSV data", e);
            throw new TextParsingException(null, "Unable to parse CSV file.  File content is not valid CSV data.", e);
        }

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