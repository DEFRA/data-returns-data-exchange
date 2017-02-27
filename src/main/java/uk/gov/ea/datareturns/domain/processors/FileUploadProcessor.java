package uk.gov.ea.datareturns.domain.processors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.ProcessorSettings;
import uk.gov.ea.datareturns.domain.exceptions.*;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.impl.EaId;
import uk.gov.ea.datareturns.domain.model.rules.FileType;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.result.*;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Processor for file uploads to the data-returns service.
 * This processor reads the information supplied in the CSV input file into a Java model and performs validation
 * using JSR303 bean validation via hibernate-validator API.
 * Files with valid content are stored using the configured storage provider in a temporary area until a subsequent
 * call to the service invokes the {@link FileCompletionProcessor} using the unique file key returned by this
 * processor.
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUploadProcessor extends AbstractReturnsProcessor<DataExchangeResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadProcessor.class);

    private final StorageProvider storage;

    private final DataSampleValidator validator;

    private final DataReturnsCSVProcessor csvProcessor;

    private InputStream inputStream;

    private String clientFilename;

    /**
     * Create a new {@link FileUploadProcessor}.
     *
     * @param processorSettings processor specific configuration settings
     * @param storage the storage provider.
     * @param validator the validator component to perform validation on the file content
     * @throws ProcessingException if a processing error occurs while attempting to process/validate the file submission
     *
     */
    @Inject
    public FileUploadProcessor(final ProcessorSettings processorSettings, final StorageProvider storage,
            final DataSampleValidator validator, final DataReturnsCSVProcessor csvProcessor)
            throws ProcessingException {
        super(processorSettings);
        this.storage = storage;
        this.validator = validator;
        this.csvProcessor = csvProcessor;
    }

    /**
     * @param inputStream the inputStream to set
     */
    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * @param clientFilename the clientFilename to set
     */
    public void setClientFilename(final String clientFilename) {
        this.clientFilename = clientFilename;
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.processors.AbstractReturnsProcessor#doProcess()
     */
    @Override
    protected DataExchangeResult doProcess() throws ProcessingException {
        try {
            if (this.inputStream == null) {
                throw new ProcessingException("Unable to process a null stream");
            }
            byte[] data = IOUtils.toByteArray(this.inputStream);
            // 2. Do some basic checks on the upload file
            checkUploadFile(this.clientFilename, data);

            // 3. Read the CSV data into a model
            final List<DataSample> model = csvProcessor.read(data);
            // Validate the model
            final ValidationErrors validationErrors = this.validator.validateModel(model);

            // Woohoo! prepare success/failure response
            final DataExchangeResult result = new DataExchangeResult(new UploadResult(this.clientFilename));

            if (validationErrors.isValid()) {
                LOGGER.debug("File '" + this.clientFilename + "' is VALID");

				/*
                 * Prepare the data for output to Emma.
				 * This involves breaking the data up into separate lists my permit number and creating an individual output file for each
				 * permit.
				 */
                final List<File> outputFiles = new ArrayList<>();
                final Map<String, EaId> outputFileIdentifiers = new HashMap<>();
                final Map<EaId, List<DataSample>> permitToRecordMap = model.stream().collect(Collectors.groupingBy(DataSample::getEaId));
                for (final Map.Entry<EaId, List<DataSample>> entry : permitToRecordMap.entrySet()) {
                    final File permitDataFile = File.createTempFile("output-" + entry.getKey().getValue().getName() + "-", ".csv",
                            this.workingFolder);
                    csvProcessor.write(entry.getValue(), permitDataFile);
                    outputFiles.add(permitDataFile);
                    outputFileIdentifiers.put(permitDataFile.getName(), entry.getKey());
                }

                // Persist the file to the configured storage provider (e.g. Amazon S3)
                final DataReturnsZipFileModel zipModel = new DataReturnsZipFileModel();
                zipModel.setInputFileName(this.clientFilename);
                zipModel.setInputData(data);
                zipModel.setOutputFiles(outputFiles);
                zipModel.setOutputFileIdentifiers(outputFileIdentifiers);
                final File zipFile = zipModel.toZipFile(this.workingFolder);

                final String fileKey = this.storage.storeTemporaryData(zipFile);
                result.getUploadResult().setFileKey(fileKey);

                // Construct a parse result to return to the client
                result.setParseResult(new ParseResult(model));
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("File '" + this.clientFilename + "' is INVALID");
                }
                for (ValidationErrorType v : validationErrors.getErrorList()) {
                    LOGGER.debug("Validation error: " + v);
                }
                result.setValidationErrors(validationErrors);
                result.setAppStatusCode(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
            }
            return result;
        } catch (final IOException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Basic file validation
     *
     * @param clientFilename the filename of the file reported by the client
     * @param data the file data that was uploaded
     * @throws AbstractValidationException if basic validation fails
     */
    private static void checkUploadFile(final String clientFilename, byte[] data) throws AbstractValidationException {
        final String fileType = FilenameUtils.getExtension(clientFilename);

        // Release 1 must be csv - do not agree with checking file extension (means nothing) but this is a requirement!
        if (!FileType.CSV.getExtension().equalsIgnoreCase(fileType)) {
            throw new FileTypeUnsupportedException("Unsupported file type '" + fileType + "'");
        }

        // Check for empty files
        if (data == null || data.length == 0) {
            throw new FileEmptyException("The uploaded file is empty");
        }
    }
}