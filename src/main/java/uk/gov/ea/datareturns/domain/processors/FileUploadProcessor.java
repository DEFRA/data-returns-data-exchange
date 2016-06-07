/**
 *
 */
package uk.gov.ea.datareturns.domain.processors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.config.ProcessorSettings;
import uk.gov.ea.datareturns.domain.exceptions.AbstractValidationException;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.exceptions.FileEmptyException;
import uk.gov.ea.datareturns.domain.exceptions.FileTypeUnsupportedException;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.model.rules.FileType;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ParseResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;
import uk.gov.ea.datareturns.util.StopWatch;

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
			final DataSampleValidator validator)
			throws ProcessingException {
		super(processorSettings);
		this.storage = storage;
		this.validator = validator;
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

			final StopWatch stopwatch = new StopWatch("data-exchange upload timer");
			stopwatch.startTask("Preparing File");

			final DataReturnsCSVProcessor csvProcessor = new DataReturnsCSVProcessor();
			final File uploadedFile = File.createTempFile("dr-upload", ".csv", this.workingFolder);

			// 1. Save upload file on server
			FileUtils.copyInputStreamToFile(this.inputStream, uploadedFile);

			// 2. Do some basic checks on the upload file
			checkUploadFile(this.clientFilename, uploadedFile);

			stopwatch.startTask("Parsing file into model");

			// 3. Read the CSV data into a model
			final CSVModel<DataSample> csvInput = csvProcessor.read(uploadedFile);

			stopwatch.startTask("Validating model");
			// Validate the model
			final ValidationErrors validationErrors = this.validator.validateModel(csvInput);

			// Woohoo! prepare success/failure response
			final DataExchangeResult result = new DataExchangeResult(new UploadResult(this.clientFilename));

			if (validationErrors.isValid()) {
				LOGGER.debug("File '" + this.clientFilename + "' is VALID");

				/*
				 * Prepare the data for output to Emma.
				 * This involves breaking the data up into separate lists my permit number and creating an individual output file for each
				 * permit.
				 */
				stopwatch.startTask("Preparing output files");
				final List<File> outputFiles = new ArrayList<>();
				final Map<EaId, List<DataSample>> permitToRecordMap = prepareOutputData(csvInput.getRecords());
				for (final Map.Entry<EaId, List<DataSample>> entry : permitToRecordMap.entrySet()) {
					final File permitDataFile = File.createTempFile("output-" + entry.getKey().getIdentifier() + "-", ".csv",
							this.workingFolder);
					csvProcessor.write(entry.getValue(), getProcessorSettings().getOutputMappingsMap(), permitDataFile);
					outputFiles.add(permitDataFile);
				}

				// Persist the file to the configured storage provider (e.g. Amazon S3)
				stopwatch.startTask("Zipping output files");
				final DataReturnsZipFileModel zipModel = new DataReturnsZipFileModel();
				zipModel.setInputFile(uploadedFile);
				zipModel.setOutputFiles(outputFiles);
				final File zipFile = zipModel.toZipFile(this.workingFolder);

				stopwatch.startTask("Persisting output files to temporary storage");
				final String fileKey = this.storage.storeTemporaryData(zipFile);
				result.getUploadResult().setFileKey(fileKey);

				// Construct a parse result to return to the client
				result.setParseResult(new ParseResult(csvInput.getRecords()));
			} else {
				LOGGER.debug("File '" + this.clientFilename + "' is INVALID");
				result.setValidationErrors(validationErrors);
				result.setAppStatusCode(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
			}

			stopwatch.stop();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(stopwatch.prettyPrint());
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
	 * @param uploadedFile the file that was uploaded
	 * @throws AbstractValidationException if basic validation fails
	 */
	private static void checkUploadFile(final String clientFilename, final File uploadedFile) throws AbstractValidationException {
		final String fileType = FilenameUtils.getExtension(clientFilename);

		// Release 1 must be csv - do not agree with checking file extension (means nothing) but this is a requirement!
		if (!FileType.CSV.getExtension().equalsIgnoreCase(fileType)) {
			throw new FileTypeUnsupportedException("Unsupported file type '" + fileType + "'");
		}

		// Check for empty files
		if (FileUtils.sizeOf(uploadedFile) == 0) {
			throw new FileEmptyException("The uploaded file is empty");
		}
	}

	/**
	 * Prepare a Map of permit numbers (key) to a {@link List} of {@link DataSample}s (value) belonging to that permit
	 *
	 * @param records the {@link List} of {@link DataSample} to prepared for output
	 * @return a {@link Map} of {@link EaId}s to a {@link List} of {@link DataSample}s
	 */
	private static Map<EaId, List<DataSample>> prepareOutputData(final List<DataSample> records) {
		final Map<EaId, List<DataSample>> recordMap = new HashMap<>();

		for (final DataSample record : records) {
			/*
			 * Build the output map
			 */
			List<DataSample> recordsForPermit = recordMap.get(record.getEaId());
			if (recordsForPermit == null) {
				recordsForPermit = new ArrayList<>();
			}
			recordsForPermit.add(record);
			recordMap.put(record.getEaId(), recordsForPermit);
		}
		return recordMap;
	}
}