package uk.gov.defra.datareturns.service.csv;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.exceptions.ApplicationExceptionType;
import uk.gov.defra.datareturns.exceptions.ValidationException;
import uk.gov.defra.datareturns.util.TextUtils;
import uk.gov.defra.datareturns.validation.payloads.datasample.fields.MonitoringDate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ECM CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
public interface ECMCSVReader {
    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
     * @return a {@link List} composed of {@link Record} objects to represent the samples/readings submitted
     * @throws IOException         if an error occurs attempting to read from the {@link InputStream}
     * @throws ValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    List<Dataset> read(InputStream inputStream) throws IOException, ValidationException;

    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param data a byte array containing DEP compliant CSV data
     * @return a {@link List} composed of {@link Record} objects to represent the samples/readings submitted
     * @throws ValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    List<Dataset> read(byte[] data) throws ValidationException;

    /**
     * Data Returns CSV reader/writer for DEP compliant CSV files.
     *
     * @author Sam Gardner-Dell
     */
    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Slf4j
    class ECMCSVProcessorImpl implements ECMCSVReader {
        private static final Pattern VALUE_PATTERN = Pattern.compile("(?<Equality>[<>]?)\\s*(?<Number>-?(\\d+\\.)?(\\d)+)");

        /**
         * Read the given source of DEP compliant CSV data into the Java model
         *
         * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
         * @return a {@link List} composed of {@link Dataset} objects to represent the samples/readings submitted
         * @throws ValidationException if a validation error occurs when attempting to read the DEP compliant CSV
         */
        @Override
        public List<Dataset> read(final InputStream inputStream) throws IOException, ValidationException {
            return read(IOUtils.toByteArray(inputStream));
        }

        /**
         * Read the given source of DEP compliant CSV data into the Java model
         *
         * @param csvData a byte array containing DEP compliant CSV data
         * @return a {@link List} composed of {@link Dataset} objects to represent the samples/readings submitted
         * @throws ValidationException if a validation error occurs when attempting to read the DEP compliant CSV
         */
        @Override
        public List<Dataset> read(final byte[] csvData) throws ValidationException {
            final CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setLineSeparatorDetectionEnabled(true);
            parserSettings.trimValues(true);

            final RowListProcessor rowProcessor = new RowListProcessor();
            parserSettings.setProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(true);

            // creates a parser instance with the given settings
            final List<Map<String, String>> data = new ArrayList<>();
            final CsvParser parser = new CsvParser(parserSettings);
            try {
                parser.parse(new ByteArrayInputStream(csvData), EncodingSupport.detectCharset(csvData));

                final String[] headers = rowProcessor.getHeaders();
                final List<String[]> rows = rowProcessor.getRows();

                for (final String[] rowData : rows) {
                    // Check for inconsistent number of fields in a row with respect to the defined headers
                    if (rowData.length != headers.length) {
                        // Row encountered with an inconsistent number of fields with respect to the header definitions.
                        throw new ValidationException(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION,
                                String.format("Record %d contains %d entries but the header has %d.", data.size(), rowData.length, headers.length));
                    }
                    final Map<String, String> values = new HashMap<>();
                    for (int i = 0; i < rowData.length; i++) {
                        values.put(headers[i], TextUtils.normalize(rowData[i]));
                    }
                    data.add(values);
                }
            } catch (final ValidationException e) {
                throw e;
            } catch (final Throwable e) {
                log.warn("Unexpected exception while parsing CSV file.", e);
                throw new ValidationException(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED,
                        "Unable to parse CSV file.  File content is not valid CSV data.");
            }
            final String[] headers = rowProcessor.getHeaders();

            // Set of headers defined in the supplied model (from the CSV file)
            final Set<String> csvHeaders = new LinkedHashSet<>();
            if (headers != null) {
                for (final String header : headers) {
                    if (!CSVField.ALL_FIELD_NAMES.contains(header)) {
                        throw new ValidationException(ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND,
                                "Unrecognised field encountered: " + header);
                    }
                    if (!csvHeaders.add(header)) {
                        throw new ValidationException(ApplicationExceptionType.HEADER_DUPLICATE_FIELD_FOUND,
                                "There are duplicate headings of the field " + header);
                    }
                }
            }

            // Check that the file contains all mandatory headers
            if (!csvHeaders.containsAll(CSVField.MANDATORY_FIELD_NAMES)) {
                throw new ValidationException(ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING,
                        "Missing fields one or more mandatory fields: " + CSVField.MANDATORY_FIELD_NAMES.toString());
            }

            // Check that the file contains records as well as a header!
            if (data.isEmpty()) {
                throw new ValidationException(ApplicationExceptionType.NO_RECORDS, "The uploaded file does not contain any records.");
            }

            final Map<String, Dataset> datasetByEaId = new HashMap<>();
            for (final Map<String, String> rowData : data) {

                // FIXME: Validate Site_Name is valid for the given EA_ID
                final String eaId = rowData.get(CSVField.EA_ID.getName());
                final String siteName = rowData.get(CSVField.Site_Name.getName());


                Dataset dataset = datasetByEaId.get(eaId);
                if (dataset == null) {
                    dataset = new Dataset();
                    dataset.setEaId(eaId);
                    datasetByEaId.put(eaId, dataset);
                }

                List<Record> records = dataset.getRecords();
                if (records == null) {
                    records = new ArrayList<>();
                    dataset.setRecords(records);
                }

                final Record record = new Record();
                record.setDataset(dataset);
                record.setReturnType(rowData.get(CSVField.Rtn_Type.getName()));

                // FIXME: Monitoring Date VALIDATION!
                final MonitoringDate monitoringDate = new MonitoringDate(rowData.get(CSVField.Mon_Date.getName()));
                if (monitoringDate.isParsed()) {
                    record.setMonitoringDate(Date.from(monitoringDate.getInstant()));
                }

                record.setMonitoringPoint(rowData.get(CSVField.Mon_Point.getName()));
                record.setParameter(rowData.get(CSVField.Parameter.getName()));

                final String valueField = rowData.get(CSVField.Value.getName());
                if (StringUtils.isNotEmpty(valueField)) {
                    final Matcher valueMatcher = VALUE_PATTERN.matcher(valueField);
                    if (valueMatcher.matches()) {
                        final Record.Equality equality = Record.Equality.forSymbol(valueMatcher.group("Equality"));
                        final BigDecimal numericValue = BigDecimal.valueOf(Double.parseDouble(valueMatcher.group("Number")));

                        record.setNumericEquality(equality);
                        record.setNumericValue(numericValue);
                    }
                }

                record.setTextValue(rowData.get(CSVField.Txt_Value.getName()));
                record.setQualifier(rowData.get(CSVField.Qualifier.getName()));
                record.setUnit(rowData.get(CSVField.Unit.getName()));
                record.setReferencePeriod(rowData.get(CSVField.Ref_Period.getName()));
                record.setMethodOrStandard(rowData.get(CSVField.Meth_Stand.getName()));
                record.setReturnPeriod(rowData.get(CSVField.Rtn_Period.getName()));
                record.setComments(rowData.get(CSVField.Comments.getName()));

                records.add(record);
            }

            return new ArrayList<>(datasetByEaId.values());
        }
    }
}
