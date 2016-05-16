package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
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
import uk.gov.ea.datareturns.domain.result.CompleteResult;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ParseResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.email.MonitorProTransportHandler;
import uk.gov.ea.datareturns.exception.application.AbstractValidationException;
import uk.gov.ea.datareturns.exception.application.FileEmptyException;
import uk.gov.ea.datareturns.exception.application.FileTypeUnsupportedException;
import uk.gov.ea.datareturns.storage.StorageProvider;
import uk.gov.ea.datareturns.storage.StorageProvider.StoredFile;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;
import uk.gov.ea.datareturns.type.FileType;

// TODO move some methods to helper classes

@Path("/data-exchange/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataExchangeResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

	@Inject
	private MiscSettings miscSettings;

	@Inject
	private StorageProvider storage;

	@Inject
	private MonitoringDataRecordValidationProcessor validator;

	@Inject
	private MonitorProTransportHandler monitorProHandler;

	public DataExchangeResource() {
	}

	/**
	 * REST method to handle Returns file upload.
	 *
	 * @param is
	 * @param fileDetail
	 * @return JSON object
	 * @throws Exception
	 */
	@POST
	@Path("/upload")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	public Response uploadFile(
			@FormDataParam("fileUpload") final InputStream is,
			@FormDataParam("fileUpload") final FormDataContentDisposition fileDetail) throws Exception {
		LOGGER.debug("/data-exchange/upload request received");

		final StopWatch stopwatch = new StopWatch("data-exchange upload timer");
		stopwatch.startTask("Preparing File");

		final File workFolder = createWorkFolder();
		final String clientFilename = getClientFilename(fileDetail);
		final File uploadedFile = File.createTempFile("dr-upload", ".csv", workFolder);

		final DataReturnsCSVProcessor csvProcessor = new DataReturnsCSVProcessor();

		// 1. Save upload file on server
		FileUtils.copyInputStreamToFile(is, uploadedFile);

		// 2. Do some basic checks on the upload file
		checkUploadFile(clientFilename, uploadedFile);

		stopwatch.startTask("Parsing file into model");

		// 3. Read the CSV data into a model
		final CSVModel<MonitoringDataRecord> csvInput = csvProcessor.read(uploadedFile);

		stopwatch.startTask("Validating model");
		// Validate the model
		final ValidationErrors validationErrors = this.validator.validateModel(csvInput);

		// Woohoo! prepare success/failure response
		final DataExchangeResult result = new DataExchangeResult(new UploadResult(fileDetail.getFileName()));

		// Default response status
		Status responseStatus = Status.OK;

		if (validationErrors.isValid()) {
			LOGGER.debug("File '" + fileDetail.getFileName() + "' is VALID");

			/*
			 * Prepare the data for output to Emma.
			 * This involves breaking the data up into separate lists my permit number and creating an individual output file for each
			 * permit.
			 */
			stopwatch.startTask("Preparing output files");
			final List<File> outputFiles = new ArrayList<>();
			final Map<EaId, List<MonitoringDataRecord>> permitToRecordMap = prepareOutputData(csvInput.getRecords());
			for (final Map.Entry<EaId, List<MonitoringDataRecord>> entry : permitToRecordMap.entrySet()) {
				final File permitDataFile = File.createTempFile("output-" + entry.getKey().getIdentifier() + "-", ".csv", workFolder);
				csvProcessor.write(entry.getValue(), permitDataFile);
				outputFiles.add(permitDataFile);
			}

			// Persist the file to the configured storage provider (e.g. Amazon S3)
			stopwatch.startTask("Zipping output files");
			final DataReturnsZipFileModel zipModel = new DataReturnsZipFileModel();
			zipModel.setInputFile(uploadedFile);
			zipModel.setOutputFiles(outputFiles);
			final File zipFile = zipModel.toZipFile(workFolder);

			stopwatch.startTask("Persisting output files to temporary storage");
			final String fileKey = this.storage.storeTemporaryData(zipFile);
			result.getUploadResult().setFileKey(fileKey);

			// Construct a parse result to return to the client
			result.setParseResult(new ParseResult(csvInput.getRecords()));

			// Passed validation, return success status
			responseStatus = Status.OK;
		} else {
			LOGGER.debug("File '" + fileDetail.getFileName() + "' is INVALID");
			result.setValidationErrors(validationErrors);
			result.setAppStatusCode(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
			responseStatus = Status.BAD_REQUEST;
		}

		stopwatch.startTask("Clearing up");
		FileUtils.deleteQuietly(workFolder);

		stopwatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopwatch.prettyPrint());
		}
		return Response.status(responseStatus).entity(result).build();
	}

	/**
	 * Complete an upload session
	 *
	 * @param orgFileKey
	 * @param userEmail
	 * @param orgFileName
	 * @param permitNo
	 * @return {@link Response} object
	 * @throws Exception
	 */
	@POST
	@Path("/complete")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	public Response completeUpload(
			@NotEmpty @FormDataParam("fileKey") final String orgFileKey,
			@NotEmpty @FormDataParam("userEmail") final String userEmail,
			@NotEmpty @FormDataParam("orgFileName") final String orgFileName) throws Exception {
		LOGGER.debug("/data-exchange/complete request received");

		final StopWatch stopwatch = new StopWatch("data-exchange complete timer");
		stopwatch.startTask("Retrieving file from temporary storage");

		final StoredFile storedFile = this.storage.retrieveTemporaryData(orgFileKey);

		final File workFolder = createWorkFolder();
		stopwatch.startTask("Extracting data files");
		final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(workFolder, storedFile.getFile());

		stopwatch.startTask("Sending data to monitor pro");
		for (final File outputFile : zipModel.getOutputFiles()) {
			this.monitorProHandler.sendNotifications(outputFile);
		}

		stopwatch.startTask("Moving data to audit store");
		final Map<String, String> metadata = new LinkedHashMap<>();
		metadata.put("originator-email", userEmail);
		metadata.put("original-filename", orgFileName);

		this.storage.moveToAuditStore(orgFileKey, metadata);

		// Delete the work folder
		stopwatch.startTask("Clearing up");
		FileUtils.deleteQuietly(workFolder);
		
		stopwatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopwatch.prettyPrint());
		}

		final DataExchangeResult result = new DataExchangeResult(new CompleteResult(orgFileKey, userEmail));
		return Response.ok(result).build();
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

	/**
	 * Create a work folder for the session
	 *
	 * @return a {@link File} pointing to a working folder
	 */
	private File createWorkFolder() throws IOException {
		final java.nio.file.Path outputPath = new File(this.miscSettings.getOutputLocation()).toPath();
		return Files.createTempDirectory(outputPath, "dr-wrk").toFile();
	}

	/**
	 * Retrieve the name of the file uploaded by the client.
	 *
	 * This method does sanitisation of the filename provided by the client including stripping any path information that may have been
	 * included (in an attempt to compromise the system)
	 *
	 * @param fileDetail the {@link FormDataContentDisposition} included with the request
	 * @return the filename reported by the client or "undefined.csv" if this data is not available.
	 */
	private static String getClientFilename(final FormDataContentDisposition fileDetail) {
		String filename = "undefined.csv";
		if (fileDetail != null && fileDetail.getFileName() != null) {
			final File file = new File(fileDetail.getFileName());
			filename = file.getName();
		}
		return filename;
	}
}