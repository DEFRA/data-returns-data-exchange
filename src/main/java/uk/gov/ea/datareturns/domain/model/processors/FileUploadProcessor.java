/**
 *
 */
package uk.gov.ea.datareturns.domain.model.processors;

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

import uk.gov.ea.datareturns.StopWatch;
import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.validation.MonitoringDataRecordValidationProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ParseResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.exception.application.AbstractValidationException;
import uk.gov.ea.datareturns.exception.application.FileEmptyException;
import uk.gov.ea.datareturns.exception.application.FileTypeUnsupportedException;
import uk.gov.ea.datareturns.exception.application.ProcessingException;
import uk.gov.ea.datareturns.storage.StorageProvider;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;
import uk.gov.ea.datareturns.type.FileType;

/**
 * @author Sam Gardner-Dell
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUploadProcessor extends AbstractReturnsProcessor<DataExchangeResult> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadProcessor.class);

	private final StorageProvider storage;

	private final MonitoringDataRecordValidationProcessor validator;

	private InputStream inputStream;

	private String clientFilename;

	/**
	 * @param miscSettings
	 * @param storage
	 * @param validator
	 * @throws ProcessingException
	 */
	@Inject
	public FileUploadProcessor(final MiscSettings miscSettings, final StorageProvider storage,
			final MonitoringDataRecordValidationProcessor validator)
			throws ProcessingException {
		super(miscSettings);
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
	 * @see uk.gov.ea.datareturns.domain.model.processors.AbstractReturnsProcessor#doProcess()
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
			final CSVModel<MonitoringDataRecord> csvInput = csvProcessor.read(uploadedFile);

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
				final Map<EaId, List<MonitoringDataRecord>> permitToRecordMap = prepareOutputData(csvInput.getRecords());
				for (final Map.Entry<EaId, List<MonitoringDataRecord>> entry : permitToRecordMap.entrySet()) {
					final File permitDataFile = File.createTempFile("output-" + entry.getKey().getIdentifier() + "-", ".csv",
							this.workingFolder);
					csvProcessor.write(entry.getValue(), permitDataFile);
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
	 * @param clientFilename
	 * @param uploadedFile
	 */
	private static void checkUploadFile(final String clientFilename, final File uploadedFile) throws AbstractValidationException {
		final String fileType = FilenameUtils.getExtension(clientFilename);

		// Release 1 must be csv
		if (!FileType.CSV.getFileType().equalsIgnoreCase(fileType)) {
			throw new FileTypeUnsupportedException("Unsupported file type '" + fileType + "'");
		}

		if (FileUtils.sizeOf(uploadedFile) == 0) {
			throw new FileEmptyException("The uploaded file is empty");
		}
	}

	/**
	 * Prepare a Map of permit numbers (key) to a {@link List} of {@link MonitoringDataRecord}s (value) belonging to that permit
	 *
	 * @param records the {@link List} of {@link MonitoringDataRecord} to prepared for output
	 * @return a {@link Map} of {@link EaId}s to a {@link List} of {@link MonitoringDataRecord}s
	 */
	private static final Map<EaId, List<MonitoringDataRecord>> prepareOutputData(final List<MonitoringDataRecord> records) {
		final Map<EaId, List<MonitoringDataRecord>> recordMap = new HashMap<>();

		for (final MonitoringDataRecord record : records) {
			/*
			 * Build the output map
			 */
			List<MonitoringDataRecord> recordsForPermit = recordMap.get(record.getEaId());
			if (recordsForPermit == null) {
				recordsForPermit = new ArrayList<>();
			}
			recordsForPermit.add(record);
			recordMap.put(record.getEaId(), recordsForPermit);
		}
		return recordMap;
	}
}
