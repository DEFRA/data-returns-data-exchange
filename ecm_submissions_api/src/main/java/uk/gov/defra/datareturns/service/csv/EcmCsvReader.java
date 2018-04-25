package uk.gov.defra.datareturns.service.csv;

import com.univocity.parsers.common.Context;
import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.data.model.upload.ParserSummary;
import uk.gov.defra.datareturns.data.model.upload.Substitution;
import uk.gov.defra.datareturns.data.model.upload.Upload;
import uk.gov.defra.datareturns.exceptions.ApplicationExceptionType;
import uk.gov.defra.datareturns.exceptions.CsvValidationException;
import uk.gov.defra.datareturns.service.MasterDataNomenclature;
import uk.gov.defra.datareturns.util.TextUtils;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;
import uk.gov.defra.datareturns.validation.service.dto.MdUniqueIdentifier;

import javax.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ECM CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
public interface EcmCsvReader {
    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
     * @return a {@link List} composed of {@link Record} objects to represent the samples/readings submitted
     * @throws IOException            if an error occurs attempting to read from the {@link InputStream}
     * @throws CsvValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    EcmCsvResult read(final String filename, InputStream inputStream) throws IOException, CsvValidationException;

    /**
     * Read the given source of DEP compliant CSV data into the Java model
     *
     * @param data a byte array containing DEP compliant CSV data
     * @return a {@link List} composed of {@link Record} objects to represent the samples/readings submitted
     * @throws CsvValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    EcmCsvResult read(final String filename, byte[] data) throws CsvValidationException;

    /**
     * Data Returns CSV reader/writer for DEP compliant CSV files.
     *
     * @author Sam Gardner-Dell
     */
    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Slf4j
    @RequiredArgsConstructor
    class EcmCsvReaderImpl implements EcmCsvReader {
        private static final Long VALUE_NOT_FOUND = -1L;
        private final MasterDataLookupService lookupService;
        private final Validator validator;

        /**
         * Read the given source of DEP compliant CSV data into the Java model
         *
         * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
         * @return a {@link List} composed of {@link Dataset} objects to represent the samples/readings submitted
         * @throws CsvValidationException if a validation error occurs when attempting to read the DEP compliant CSV
         */
        @Override
        public EcmCsvResult read(final String filename, final InputStream inputStream) throws IOException, CsvValidationException {
            return read(filename, IOUtils.toByteArray(inputStream));
        }

        /**
         * Read the given source of DEP compliant CSV data into the Java model
         *
         * @param csvData a byte array containing DEP compliant CSV data
         * @return a {@link List} composed of {@link Dataset} objects to represent the samples/readings submitted
         * @throws CsvValidationException if a validation error occurs when attempting to read the DEP compliant CSV
         */
        @Override
        public EcmCsvResult read(final String filename, final byte[] csvData) throws CsvValidationException {
            log.info("Reading file " + filename);
            final List<EcmCsvRecord> data = readFileData(csvData);
            log.info("Checking file " + filename);
            return createResult(filename, data);
        }

        private EcmCsvResult createResult(final String filename, final List<EcmCsvRecord> data) throws CsvValidationException {
            // Check that the file contains records as well as a header!
            if (data.isEmpty()) {
                throw new CsvValidationException(ApplicationExceptionType.NO_RECORDS, null, "The uploaded file does not contain any records.");
            }

            final Set<Substitution> substitutions = new LinkedHashSet<>();
            final Map<String, Dataset> datasetsByPermit = new LinkedHashMap<>();

            final Upload upload = new Upload();
            upload.setParserSummary(new LinkedHashSet<>());

            log.info("Extracting records from " + filename);
            final EcmCsvResult result = new EcmCsvResult(data);

            for (final EcmCsvRecord csvRecord : data) {
                validator.validate(csvRecord).forEach(result::addViolation);

                final String eaIdString = csvRecord.getEaId();
                final MdUniqueIdentifier eaId = MasterDataNomenclature.resolveMasterDataEntity(lookupService, MasterDataEntity.UNIQUE_IDENTIFIER,
                        MdUniqueIdentifier.class, eaIdString, (value, resolved) -> substitutions.add(
                                Substitution.builder()
                                        .field(EcmCsvField.EA_ID.getFieldName())
                                        .value(value)
                                        .resolved(resolved).build()));

                final Dataset dataset = datasetsByPermit.computeIfAbsent(eaIdString, v -> {
                    final Dataset ds = new Dataset();
                    ds.setEaId(StringUtils.isEmpty(eaIdString) ? null : getId(eaId));
                    ds.setUpload(upload);
                    return ds;
                });

                final Record record = csvRecord.toPersisentEntity(dataset, ((field, inputValue) -> {
                    if (StringUtils.isEmpty(inputValue)) {
                        return null;
                    }

                    return getId(MasterDataNomenclature.resolveMasterDataEntity(lookupService, field.getMasterDataEntity(),
                            field.getMasterDataEntity().getDefaultType(), inputValue, (value, resolved) -> substitutions.add(
                                    Substitution.builder()
                                            .field(field.getFieldName())
                                            .value(value)
                                            .resolved(resolved).build())));
                }));
                result.bind(record, csvRecord);
                dataset.addRecord(record);

                upload.getParserSummary().add(ParserSummary.builder()
                        .submittedEaId(eaIdString)
                        .resolvedEaId(eaId != null ? eaId.getNomenclature() : null)
                        .siteName(csvRecord.getSiteName())
                        .build());
            }
            log.info("Validating persistent model for " + filename);
            for (final Dataset dataset : datasetsByPermit.values()) {
                validator.validate(dataset).forEach(result::addViolation);
            }

            if (!result.hasViolations()) {
                upload.setFilename(filename);
                upload.setDatasets(new LinkedHashSet<>(datasetsByPermit.values()));
                upload.setSubstitutions(substitutions);
                result.setUpload(upload);
            }

            return result;
        }

        /**
         * Read the body of the specified CSV data into a {@link List} containing a Map of row headings to their respective row value
         *
         * @param csvData the csv data to be read
         * @return a {@link List} containing a Map of row headings to their respective row value
         * @throws CsvValidationException if any structural errors were found attempting to read the csv
         */
        private List<EcmCsvRecord> readFileData(final byte[] csvData) throws CsvValidationException {
            final CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setLineSeparatorDetectionEnabled(true);
            parserSettings.trimValues(true);

            final BeanListProcessor<EcmCsvRecord> rowProcessor = new BeanListProcessor<EcmCsvRecord>(EcmCsvRecord.class) {
                @Override
                public void processStarted(final ParsingContext context) {
                    super.processStarted(context);

                    // Set of headers defined in the supplied model (from the CSV file)
                    final Set<String> parsedHeaders = new LinkedHashSet<>();
                    if (context.headers() != null) {
                        final Set<String> headerSet = new HashSet<>(Arrays.asList(context.headers()));

                        // If there are no recognisable headers on row 1, then we throw a file structure exception
                        if (SetUtils.intersection(headerSet, EcmCsvField.ALL_FIELD_NAMES).isEmpty()) {
                            throw new CsvValidationException(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION, 1,
                                    "Unable to parse CSV file.  File content is not valid DEP CSV data.");
                        }

                        for (final String header : context.headers()) {
                            if (!EcmCsvField.ALL_FIELD_NAMES.contains(header)) {
                                throw new CsvValidationException(ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND, 1,
                                        "Unrecognised field encountered: " + header);
                            }
                            if (!parsedHeaders.add(header)) {
                                throw new CsvValidationException(ApplicationExceptionType.HEADER_DUPLICATE_FIELD_FOUND, 1,
                                        "There are duplicate headings of the field " + header);
                            }
                        }
                    }

                    // Check that the file contains all mandatory headers
                    if (context.headers() == null || !parsedHeaders.containsAll(EcmCsvField.MANDATORY_FIELD_NAMES)) {
                        throw new CsvValidationException(ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING, 1,
                                "Missing fields one or more mandatory fields: " + EcmCsvField.MANDATORY_FIELD_NAMES.toString());
                    }
                }

                @Override
                public EcmCsvRecord createBean(final String[] row, final Context context) {
                    // Check for inconsistent number of fields in a row with respect to the defined headers
                    if (row.length != context.headers().length) {
                        // Row encountered with an inconsistent number of fields with respect to the header definitions.
                        final Long lineNo = context.currentRecord() + 1;
                        throw new CsvValidationException(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION, lineNo.intValue(),
                                String.format("Record %d contains %d entries but the header has %d.", context.currentRecord(), row.length,
                                        context.headers().length));
                    }

                    final String[] normalizedRow = Arrays.stream(row).map(TextUtils::normalize).toArray(String[]::new);
                    return super.createBean(normalizedRow, context);
                }
            };
            parserSettings.setProcessor(rowProcessor);

            // creates a parser instance with the given settings
            final CsvParser parser = new CsvParser(parserSettings);
            try {
                parser.parse(new ByteArrayInputStream(csvData), EncodingSupport.detectCharset(csvData));
                return rowProcessor.getBeans();
            } catch (final DataProcessingException e) {
                if (e.getCause() instanceof CsvValidationException) {
                    final CsvValidationException cause = (CsvValidationException) e.getCause();
                    throw cause;
                }
                throw e;
            } catch (final CsvValidationException e) {
                throw e;
            } catch (final Throwable e) {
                log.warn("Unexpected exception while parsing CSV file.", e);
                throw new CsvValidationException(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION, 1, "Unable to parse CSV file");
            }
        }

        private <T extends MdBaseEntity> Long getId(final T mdEntity) {
            if (mdEntity != null) {
                return Long.parseLong(MasterDataLookupService.getResourceId(mdEntity));
            }
            return VALUE_NOT_FOUND;
        }
    }
}
