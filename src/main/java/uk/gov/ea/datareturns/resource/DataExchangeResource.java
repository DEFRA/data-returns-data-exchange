package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.DateFormat;
import uk.gov.ea.datareturns.domain.model.validation.MonitoringDataRecordValidationProcessor;
import uk.gov.ea.datareturns.domain.result.CompleteResult;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ParseResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.email.MonitorProEmailer;
import uk.gov.ea.datareturns.exception.application.DRFileEmptyException;
import uk.gov.ea.datareturns.exception.application.DRFileKeyMismatchException;
import uk.gov.ea.datareturns.exception.application.DRFileTypeUnsupportedException;
import uk.gov.ea.datareturns.exception.system.DRSystemException;
import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.storage.StorageProvider;
import uk.gov.ea.datareturns.storage.StorageProvider.StoredFile;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;
import uk.gov.ea.datareturns.type.FileType;

// TODO move some methods to helper classes

@Path("/data-exchange/")
public class DataExchangeResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);
	private final DataExchangeConfiguration config;
	private final StorageProvider storage;

	public DataExchangeResource(final DataExchangeConfiguration config, final StorageProvider storage) {
		this.config = config;
		this.storage = storage;
	}

	/**
	 * REST method to handle Returns file upload.
	 *
	 * @param is
	 * @param fileDetail
	 * @return JSON object
	 */
	@POST
	@Path("/upload")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	@Timed
	public Response uploadFile(
			@FormDataParam("fileUpload") final InputStream is,
			@FormDataParam("fileUpload") final FormDataContentDisposition fileDetail) {
		final MiscSettings settings = this.config.getMiscSettings();
		final File uploadedFile = new File(settings.getUploadedLocation(), fileDetail.getFileName());
		final DataReturnsCSVProcessor csvProcessor = new DataReturnsCSVProcessor(this.config);

		// 1. Save upload file on server
		try {
			FileUtils.copyInputStreamToFile(is, uploadedFile);
		} catch (final IOException e) {
			throw new DRSystemException(e, "Unable to save the uploaded file to disk.");
		}

		// 2. Do some basic checks on the upload file
		checkUploadFile(uploadedFile);

		// 3. Read the CSV data into a model
		final CSVModel<MonitoringDataRecord> csvInput = csvProcessor.readCSVFile(uploadedFile);

		// Validate the model
		final ValidationErrors validationErrors = MonitoringDataRecordValidationProcessor.validateModel(csvInput);

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
			final List<File> outputFiles = new ArrayList<>();
			final Map<String, List<MonitoringDataRecord>> permitToRecordMap = prepareOutputData(csvInput.getRecords());
			final File workFolder = createWorkFolder();
			permitToRecordMap.forEach((permitNumber, recordList) -> {
				try {
					final File permitDataFile = File.createTempFile("output-" + permitNumber + "-", ".csv", workFolder);
					csvProcessor.writeCSVFile(recordList, permitDataFile);
					outputFiles.add(permitDataFile);
				} catch (final IOException e) {
					throw new DRSystemException(e, "Unable to write output data to temporary file store");
				}
			});

			// Persist the file to the configured storage provider (e.g. Amazon S3)
			try {
				final DataReturnsZipFileModel zipModel = new DataReturnsZipFileModel();
				zipModel.setInputFile(uploadedFile);
				zipModel.setOutputFiles(outputFiles);
				final File zipFile = zipModel.toZipFile(workFolder);
				final String fileKey = this.storage.storeTemporaryData(zipFile);
				result.getUploadResult().setFileKey(fileKey);
			} catch (final IOException | StorageException e) {
				throw new DRSystemException(e);
			}

			// Delete the local temporary files now that they have been persisted to the storage provider
			final List<File> filesToDelete = new ArrayList<>();
			filesToDelete.add(uploadedFile);
			filesToDelete.add(workFolder);
			filesToDelete.forEach((file) -> {
				if (!FileUtils.deleteQuietly(file)) {
					file.deleteOnExit();
				}
			});

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
		return Response.status(responseStatus).entity(result).build();
	}

	/**
	 * Complete an upload session
	 *
	 * @param orgFileKey
	 * @param userEmail
	 * @param orgFileName
	 * @param permitNo
	 * @return
	 */
	@POST
	@Path("/complete")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	@Timed
	public Response completeUpload(
			@NotEmpty @FormDataParam("fileKey")	final String orgFileKey,
			@NotEmpty @FormDataParam("userEmail") final String userEmail,
			@NotEmpty @FormDataParam("orgFileName") final String orgFileName) {
		LOGGER.debug("/data-exchange/complete request received");

		StoredFile storedFile = null;
		try {
			storedFile = this.storage.retrieveTemporaryData(orgFileKey);
		} catch (final StorageKeyMismatchException e) {
			throw new DRFileKeyMismatchException("Unable to retrieve a file with the provided key " + orgFileKey);
		} catch (final StorageException e) {
			throw new DRSystemException(e, "Unable to retrieve a file with the provided key " + orgFileKey);
		}

		final File workFolder = createWorkFolder();

		try {
			final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(workFolder, storedFile.getFile());
			final MonitorProEmailer emailer = new MonitorProEmailer(this.config);
			
			for (final File outputFile : zipModel.getOutputFiles()) {
				emailer.sendNotifications(outputFile);
			}

			final Map<String, String> metadata = new LinkedHashMap<>();
			metadata.put("originator-email", userEmail);
			metadata.put("original-filename", orgFileName);

			this.storage.moveToAuditStore(orgFileKey, metadata);
		} catch (final IOException e) {
			throw new DRSystemException(e, "Unable to process returns files identified by " + orgFileKey);
		} catch (final StorageException e) {
			throw new DRSystemException(e, e.getMessage());
		}

		final DataExchangeResult result = new DataExchangeResult(new CompleteResult(orgFileKey, userEmail));
		return Response.ok(result).build();
	}

	/**
	 * Basic file validation
	 *
	 * @param uploadedFile
	 */
	private static void checkUploadFile(final File uploadedFile) {
		final String fileType = FilenameUtils.getExtension(uploadedFile.getName());

		// Release 1 must be csv
		if (!FileType.CSV.getFileType().equalsIgnoreCase(fileType)) {
			throw new DRFileTypeUnsupportedException("Unsupported file type '" + fileType + "'");
		}

		if (FileUtils.sizeOf(uploadedFile) == 0) {
			throw new DRFileEmptyException("The uploaded file is empty");
		}
	}

	/**
	 * Formats data as required for output to the Emma database (currently only Date entry formatting)
	 *
	 * Prepare a Map of permit numbers (key) to a {@link List} of {@link MonitoringDataRecord}s (value) belonging to that permit
	 *
	 * @param records the {@link List} of {@link MonitoringDataRecord} to prepared for output
	 * @return a {@link Map} of permit numbers to a {@link List} of {@link MonitoringDataRecord}s
	 */
	private static final Map<String, List<MonitoringDataRecord>> prepareOutputData(final List<MonitoringDataRecord> records) {
		final Map<String, List<MonitoringDataRecord>> recordMap = new HashMap<>();

		for (final MonitoringDataRecord record : records) {
			/*
			 * Prepare records for output
			 */
			// Date and time processing
			record.setMonitoringDate(DateFormat.toStandardFormat(record.getMonitoringDate()));

			/*
			 * Build the output map
			 */
			List<MonitoringDataRecord> recordsForPermit = recordMap.get(record.getPermitNumber());
			if (recordsForPermit == null) {
				recordsForPermit = new ArrayList<>();
			}
			recordsForPermit.add(record);
			recordMap.put(record.getPermitNumber(), recordsForPermit);
		}
		return recordMap;
	}

	/**
	 * Create a work folder for the session
	 * 
	 * @return a {@link File} pointing to a working folder
	 */
	private final File createWorkFolder() {
		final MiscSettings settings = this.config.getMiscSettings();
		File workFolder = null;

		while (workFolder == null || workFolder.exists()) {
			workFolder = new File(settings.getOutputLocation(), UUID.randomUUID().toString());
		}

		try {
			FileUtils.forceMkdir(workFolder);
		} catch (final IOException e) {
			throw new DRSystemException(e, "Unable to create working folder");
		}
		return workFolder;
	}
}