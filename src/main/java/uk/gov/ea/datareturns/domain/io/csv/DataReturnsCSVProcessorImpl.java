package uk.gov.ea.datareturns.domain.io.csv;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.exceptions.*;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.util.EncodingSupport;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Returns CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataReturnsCSVProcessorImpl implements DataReturnsCSVProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataReturnsCSVProcessor.class);

    private DataSampleBeanWriterProcessor writerProcessor;

    @Inject
    public DataReturnsCSVProcessorImpl(final DataSampleBeanWriterProcessor writerProcessor) {
        this.writerProcessor = writerProcessor;
    }

    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
     * @return a {@link List} composed of {@link DataSample} objects to represent the samples/readings submitted
     * @throws AbstractValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    @Override public List<DataSample> read(InputStream inputStream) throws IOException, AbstractValidationException {
        return read(IOUtils.toByteArray(inputStream));
    }

    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param data a byte array containing DEP compliant CSV data
     * @return a {@link List} composed of {@link DataSample} objects to represent the samples/readings submitted
     * @throws AbstractValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    @Override public List<DataSample> read(byte[] data) throws AbstractValidationException {
        final BeanConversionProcessor<DataSample> rowProcessor = new BeanConversionProcessor<>(DataSample.class);
        final CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.trimValues(true);
        parserSettings.setProcessor(rowProcessor);

        // creates a parser instance with the given settings
        final CsvParser parser = new CsvParser(parserSettings);
        try {
            parser.parse(new ByteArrayInputStream(data), EncodingSupport.detectCharset(data));
        } catch (final InconsistentRowException e) {
            // Row encountered with an inconsistent number of fields with respect to the header definitions.
            throw new FileStructureException(e.getMessage());
        } catch (final Throwable e) {
            LOGGER.warn("Unexpected exception while parsing CSV file.", e);
            throw new FileTypeUnsupportedException("Unable to parse CSV file.  File content is not valid CSV data.");
        }
        final String[] headers = rowProcessor.getHeaders();

        // Set of headers defined in the supplied model (from the CSV file)
        final Set<String> csvHeaders = new LinkedHashSet<>();
        if (headers != null) {
            for (String header : headers) {
                if (!FieldDefinition.ALL_FIELD_NAMES.contains(header)) {
                    throw new UnrecognisedFieldException("Unrecognised field encountered: " + header);
                }
                if (!csvHeaders.add(header)) {
                    throw new DuplicateFieldException("There are duplicate headings of the field " + header);
                }
            }
        }

        // Check that the file contains all mandatory headers
        if (!csvHeaders.containsAll(FieldDefinition.MANDATORY_FIELD_NAMES)) {
            throw new MandatoryFieldMissingException(
                    "Missing fields one or more mandatory fields: " + FieldDefinition.MANDATORY_FIELD_NAMES.toString());
        }

        // Check that the file contains records as well as a header!
        List<DataSample> records = rowProcessor.getBeans();
        if (records.isEmpty()) {
            throw new NoRecordsFoundException("The uploaded file does not contain any records.");
        }
        return records;
    }


    /**
     * Writes a CSV file based on the mappings specified in the configuration file.
     *
     * @param records the data returns records to be written
     * @param csvFile a reference to the {@link File} to be written
     */
    @Override public void write(final List<DataSample> records, final File csvFile) {
        final CsvWriterSettings settings = new CsvWriterSettings();
        writerProcessor.configure(settings);
        final CsvWriter writer = new CsvWriter(csvFile, Charset.forName("windows-1252"), settings);
        // Write the record headers of this file
        writer.writeHeaders();

        try {
            for (DataSample record : records) {
                writerProcessor.write(writer, record);
            }
        } finally {
            writer.close();
        }
    }
}