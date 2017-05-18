package uk.gov.ea.datareturns.domain.io.csv;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.exceptions.*;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldDefinition;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Data Returns CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataReturnsCSVProcessorImpl implements DataReturnsCSVProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataReturnsCSVProcessor.class);

    private static final Set<Charset> SUPPORTED_CHARSETS = new HashSet<>(Arrays.asList(new Charset[] {
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_16,
            StandardCharsets.UTF_16LE,
            StandardCharsets.UTF_16BE,
            StandardCharsets.ISO_8859_1,
            Charset.forName("ISO-8859-15"),
            Charset.forName("windows-1252")
    }));

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
            parser.parse(new ByteArrayInputStream(data), detectCharset(data));
        } catch (final InconsistentRowException e) {
            // Row encountered with an inconsistent number of entityfields with respect to the header definitions.
            throw new FileStructureException(e.getMessage());
        } catch (final Throwable e) {
            LOGGER.warn("Unexpected exception while parsing CSV file.", e);
            throw new FileTypeUnsupportedException("Unable to parseJsonArray CSV file.  File content is not valid CSV data.");
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
                    "Missing entityfields one or more mandatory entityfields: " + FieldDefinition.MANDATORY_FIELD_NAMES.toString());
        }

        // Check that the file contains records as well as a header!
        List<DataSample> records = rowProcessor.getBeans();
        if (records.isEmpty()) {
            throw new NoRecordsFoundException("The uploaded file does not contain any records.");
        }
        return records;
    }

    /**
     * Attempts to detect the character set used to encode the given byte array.
     *
     * Assumes UTF-8 if the character set cannot be automatically detected (or if the data contains no specially encoded characters)
     *
     * @param data the byte array to test
     * @return the correct character set used to encode the data (defaults to UTF8 if the charset cannot be detected)
     */
    private Charset detectCharset(byte[] data) {
        UniversalDetector detector = new UniversalDetector();
        detector.handleData(data);
        detector.dataEnd();

        // Default to expect UTF-8 encoded data.
        Charset charset = StandardCharsets.UTF_8;
        if (detector.getDetectedCharset() != null) {
            try {
                Charset detected = Charset.forName(detector.getDetectedCharset());
                if (SUPPORTED_CHARSETS.contains(detected)) {
                    charset = detected;
                }
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unable to load system charset for the type detected - " + detector.getDetectedCharset());
            }
        }
        return charset;
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
        final CsvWriter writer = new CsvWriter(csvFile, settings);
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