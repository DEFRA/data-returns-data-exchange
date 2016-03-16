package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static uk.gov.ea.datareturns.helper.CommonHelper.isLocalEnvironment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;
import uk.gov.ea.datareturns.config.EmmaDatabaseSettings;
import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.config.RedisSettings;
import uk.gov.ea.datareturns.config.S3ProxySettings;
import uk.gov.ea.datareturns.dao.PermitDAO;
import uk.gov.ea.datareturns.domain.dataexchange.EmmaDatabase;
import uk.gov.ea.datareturns.domain.io.csv.CSVHeaderValidator;
import uk.gov.ea.datareturns.domain.io.csv.CSVModel;
import uk.gov.ea.datareturns.domain.io.csv.CSVReader;
import uk.gov.ea.datareturns.domain.io.csv.CSVWriter;
import uk.gov.ea.datareturns.domain.io.csv.exceptions.HeaderFieldMissingException;
import uk.gov.ea.datareturns.domain.io.csv.exceptions.HeaderFieldUnrecognisedException;
import uk.gov.ea.datareturns.domain.io.csv.exceptions.ValidationException;
import uk.gov.ea.datareturns.domain.io.csv.settings.CSVReaderSettings;
import uk.gov.ea.datareturns.domain.io.csv.settings.CSVWriterSettings;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.types.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.model.validation.MonitoringDataRecordValidationProcessor;
import uk.gov.ea.datareturns.domain.result.CompleteResult;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ParseResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationResult;
import uk.gov.ea.datareturns.exception.application.DRFileEmptyException;
import uk.gov.ea.datareturns.exception.application.DRFileTypeUnsupportedException;
import uk.gov.ea.datareturns.exception.application.DRHeaderFieldUnrecognisedException;
import uk.gov.ea.datareturns.exception.application.DRHeaderMandatoryFieldMissingException;
import uk.gov.ea.datareturns.exception.application.DRPermitNotRecognisedException;
import uk.gov.ea.datareturns.exception.application.DRPermitNotUniqueException;
import uk.gov.ea.datareturns.exception.application.DRPermitNumberMissingException;
import uk.gov.ea.datareturns.exception.system.DRIOException;
import uk.gov.ea.datareturns.exception.system.DRSystemException;
import uk.gov.ea.datareturns.helper.DataExchangeHelper;
import uk.gov.ea.datareturns.helper.FileUtilsHelper;
import uk.gov.ea.datareturns.storage.FileStorage;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;
import uk.gov.ea.datareturns.type.FileType;

// TODO move some methods to helper classes

@Path("/data-exchange/")
public class DataExchangeResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);
	private DataExchangeConfiguration config;
	private PermitDAO permitDAO;

	private FileStorage fileStorage;

	public DataExchangeResource(DataExchangeConfiguration config, PermitDAO permitDAO) {
		this.config = config;
		this.permitDAO = permitDAO;

		RedisSettings redisSettings = config.getFileStorageSettings().getRedisSettings();

		String environment = config.getMiscSettings().getEnvironment();
		String redisHost = redisSettings.getHost();
		int redisPort = redisSettings.getPort();

		if (!isLocalEnvironment(environment)) {
			S3ProxySettings s3Settings = config.getS3Settings();
			String s3Type = s3Settings.getType();
			String s3Host = s3Settings.getHost();
			int s3Port = s3Settings.getPort();

			this.fileStorage = new FileStorage(environment, redisHost, redisPort, s3Type, s3Host, s3Port);
		} else {
			this.fileStorage = new FileStorage(environment, redisHost, redisPort);
		}
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
	@SuppressWarnings("ucd")
	public Response uploadFile(@FormDataParam("fileUpload") InputStream is,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail) {
		LOGGER.debug("/data-exchange/upload request received");

		final MiscSettings settings = config.getMiscSettings();
		final File uploadedFile = new File(settings.getUploadedLocation(), fileDetail.getFileName());

		// 1. Save upload file on server
		FileUtilsHelper.saveFile(is, uploadedFile);
		
		// 2. Validate upload file
		validateUploadFile(uploadedFile);

		// 3. Read the CSV data into a model
		final CSVModel<MonitoringDataRecord> model = readCSVFile(uploadedFile);
		
		// 4. Apply Permit number validations
		validatePermitNo(model);
		
		// Validate the model
		final ValidationResult validationResult = MonitoringDataRecordValidationProcessor.validateModel(model);

		// Woohoo! prepare success/failure response
		final UploadResult uploadResult = new UploadResult();
		uploadResult.setFileName(fileDetail.getFileName());
		final DataExchangeResult result = new DataExchangeResult(uploadResult);

		
		// Default response status
		Status responseStatus = Status.OK;

		if (validationResult.isValid()) {
			LOGGER.debug("File '" + fileDetail.getFileName() + "' is VALID");
			
			// Process the data to do any modifications necessary before outputting to Emma.  For example - ensure all output dates in international format.
			prepareOutputData(model.getRecords());
			
			// Write out a CSV file containing ALL headings 
			final File validFileOutputDir = new File(settings.getOutputLocation());
			final File outputFile = new File(validFileOutputDir, uploadedFile.getName());
			writeCSVFile(model.getRecords(), outputFile);
			
			final ParseResult parseResult = new ParseResult();
			// This should never be empty or it would have failed validation, but rather not have an ArrayIndexOutOfBoundsException anyway...
			if (!model.getRecords().isEmpty()) {
				MonitoringDataRecord record = model.getRecords().get(0);
				parseResult.setPermitNumber(record.getPermitNumber());
				parseResult.setSiteName(record.getSiteName());
				parseResult.setReturnType(record.getReturnType());
			}
			result.setParseResult(parseResult);
			
			uploadResult.setFileKey(fileStorage.saveValidFile(outputFile.getAbsolutePath()));
			
			// Passed validation, return success status
			responseStatus = Status.OK;
		} else {
			LOGGER.debug("File '" + fileDetail.getFileName() + "' is INVALID");

			// Store the original, unchanged, file that was uploaded
			uploadResult.setFileKey(fileStorage.saveInvalidFile(uploadedFile.getAbsolutePath()));
			result.setValidationResult(validationResult);
			
			result.setAppStatusCode(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
			responseStatus = Status.BAD_REQUEST;
		}
		return Response.status(responseStatus).entity(result).build();
	}

	// TODO validate entity would be better?
	@POST
	@Path("/complete")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	@Timed
	public Response completeUpload(@NotEmpty @FormDataParam("fileKey") String orgFileKey,
			@NotEmpty @FormDataParam("userEmail") String userEmail,
			@NotEmpty @FormDataParam("orgFileName") String orgFileName,
			@NotEmpty @FormDataParam("permitNo") String permitNo) {
		LOGGER.debug("/data-exchange/complete request received");

		MiscSettings settings = config.getMiscSettings();

		String outputLocation = settings.getOutputLocation();
		String fileLocation = fileStorage.retrieveValidFileByKey(orgFileKey, outputLocation);

		sendNotifications(fileLocation, permitNo);

		CompleteResult completeResult = new CompleteResult();

		// TODO only really need to return app status code - leaving these in
		// for now though
		completeResult.setFileKey(orgFileKey);
		completeResult.setUserEmail(userEmail);

		DataExchangeResult result = new DataExchangeResult(completeResult);

		return Response.ok(result).build();
	}

	/**
	 * Basic file validation
	 * 
	 * @param uploadedFile
	 */
	private static void validateUploadFile(File uploadedFile) {
		String fileType = FilenameUtils.getExtension(uploadedFile.getName());

		// Release 1 must be csv
		if (!FileType.CSV.getFileType().equalsIgnoreCase(fileType)) {
			throw new DRFileTypeUnsupportedException("Unsupported file type '" + fileType + "'");
		}
		
		if (FileUtils.sizeOf(uploadedFile) == 0) {
			throw new DRFileEmptyException("The uploaded file is empty");
		}
		
	}


	/**
	 * Apply Permit number validations
	 * 
	 */
	private void validatePermitNo(CSVModel<MonitoringDataRecord> model) {
		LOGGER.debug("Performing Permit No validations");
		
		// Apply permit validation
		final Set<String> permitNumberSet = model.getRecords()
				.stream()
				.map(MonitoringDataRecord::getPermitNumber)
				.collect(Collectors.toSet());
		
		if (permitNumberSet.isEmpty()) {
			throw new DRPermitNumberMissingException("No permits were found in the uploaded file.");
		} else if (permitNumberSet.size() > 1) {
			throw new DRPermitNotUniqueException("Multiple Permits found in the uploaded file.");
		}
		// We can safely assume that iterator().next() will not fail as we have already checked that there are exactly one permits in the set
		String permitNo = permitNumberSet.iterator().next();

		// Validate the permit no format even though it's valid as exists in permit list but
		// need to make sure it'll work later when determining database name for email subject
		if (!DataExchangeHelper.isNumericPermitNo(permitNo) 
				&& !DataExchangeHelper.isAlphaNumericPermitNo(permitNo)) {
			throw new DRPermitNotRecognisedException("Permit no '" + permitNo + "' is invalid");
		}

		if (permitDAO.findByPermitNumber(permitNo) == null) {
			throw new DRPermitNotRecognisedException("Permit no '" + permitNo + "' not found");
		}

		LOGGER.debug("Permit is valid'");
	}
	
	/**
	 * Send notifications upload is complete - currently just email to
	 * MonitorPro with csv attachment
	 * 
	 * @param attachmentLocation
	 * @param permitNo
	 * @param orgFileName
	 */
	// TODO move to own class hierarchy - also needs tests
	private void sendNotifications(String attachmentLocation, String permitNo) {
		LOGGER.debug("Sending Email with attachment '" + attachmentLocation + "'");

		EmmaDatabaseSettings dbSettings = this.config.getEmmaDatabaseSettings();

		try {
			final EmailSettings settings = this.config.getEmailsettings();
			final MultiPartEmail email = new MultiPartEmail();
			final EmmaDatabase type = DataExchangeHelper.getDatabaseTypeFromPermitNo(permitNo);
			String subject = dbSettings.getDatabaseName(type);

			email.setHostName(settings.getHost());
			email.setSmtpPort(settings.getPort());

			email.setSubject(subject);
			email.addTo(settings.getEmailTo());
			email.setFrom(settings.getEmailFrom());
			email.setMsg(settings.getBodyMessage());
			email.setStartTLSEnabled(settings.isTls());

			EmailAttachment attachment = new EmailAttachment();
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Data Returns File Upload");
			attachment.setName("Environment Agency");

			File fileAttachment = new File(attachmentLocation);
			attachment.setPath(fileAttachment.getAbsolutePath());

			email.attach(fileAttachment);

			LOGGER.debug("Email details - ");
			LOGGER.debug("  host - " + settings.getHost());
			LOGGER.debug("  port - " + settings.getPort());
			LOGGER.debug("  subject - " + subject);
			LOGGER.debug("  emailTo - " + settings.getEmailTo());
			LOGGER.debug("  emailFrom - " + settings.getEmailFrom());
			LOGGER.debug("  tls - " + settings.isTls());
			LOGGER.debug("  bodyMessage - " + settings.getBodyMessage());
			LOGGER.debug("  attached file = " + attachment.getPath());

			email.send();
		} catch (EmailException e1) {
			throw new DRSystemException(e1, "Failed to send email to MonitorPro");
		} catch (Exception e2) {
			throw new DRSystemException(e2, "Failed to send email to MonitorPro");
		}

		LOGGER.debug("Email sent");
	}
	
	/**
	 * Parse the given file as a CSV and return a Java model with the result.
	 * 
	 * @param file
	 * @return
	 */
	private final CSVModel<MonitoringDataRecord> readCSVFile(File csvFile) {
		final MiscSettings settings = config.getMiscSettings();
		final Character csvDelimiter = settings.getCSVSeparatorCharacter();
		
		// Create a validator for the CSV headings
		final CSVHeaderValidator validator = new CSVHeaderValidator() {
			@Override
			public void validateHeaders(Map<String, Integer> headerMap) throws ValidationException {
				// Get working sets for the list of all headers and the list of mandatory headers
				final Set<String> allHeaders = DataReturnsHeaders.getAllHeadings();
				final Set<String> mandatoryHeaders = DataReturnsHeaders.getMandatoryHeadings();
				// Set of headers defined in the supplied model (from the CSV file)
				final Set<String> csvHeaders = headerMap.keySet();
				
				// If we remove the CSV file's headers from the set of mandatory headers then the mandatory headers set should be empty
				// if they have defined everything that they should have.
				mandatoryHeaders.removeAll(csvHeaders);
				if (!mandatoryHeaders.isEmpty()) {
					throw new HeaderFieldMissingException("Missing fields: " + mandatoryHeaders.toString());
				}
				
				// Create a temporary set (which we can modify) of the fields defined in the CSV file
				final Set<String> tempCsvHeaderSet = new HashSet<>(csvHeaders);
				// Remove the set of all known headers from the temporary CSV file header list.  If the resultant set is not empty, then 
				// headers have been defined in the CSV file which are not allowed by the system.
				tempCsvHeaderSet.removeAll(allHeaders);
				if (!tempCsvHeaderSet.isEmpty()) {
					throw new HeaderFieldUnrecognisedException("Unrecognised field(s) encountered: " + tempCsvHeaderSet.toString());
				} 
				
			}
		};
		// Configure a CSV reader with our settings and validator - map the CSV to the MonitoringDataRecord class
		final CSVReaderSettings csvReaderSettings= new CSVReaderSettings(csvDelimiter, validator);		
		csvReaderSettings.setTrimWhitespace(true);
		final CSVReader<MonitoringDataRecord> csvReader = new CSVReader<>(MonitoringDataRecord.class, csvReaderSettings);
		
		try {
			return csvReader.parseCSV(csvFile);
		} catch (HeaderFieldMissingException e) {
			// CSV failed to parse due to a missing header field
			throw new DRHeaderMandatoryFieldMissingException(e.getMessage());
		} catch (HeaderFieldUnrecognisedException e) {
			// CSV failed to parse due to an unexpected header field
			throw new DRHeaderFieldUnrecognisedException(e.getMessage());
		} catch (ValidationException e) {
			throw new DRFileTypeUnsupportedException("Unable to parse CSV file.  File content is not valid CSV data.");
		} catch (IOException e) {
			throw new DRSystemException(e, "Failed to parse CSV file.");
		}
	}
	
	/**
	 * Prepare the list of {@link MonitoringDataRecord} entries for output
	 * 
	 * Formats data as required for output to the Emma datrabase
	 * 
	 * @param records the {@link List} of {@link MonitoringDataRecord} to prepared for output
	 */
	private static final void prepareOutputData(final List<MonitoringDataRecord> records) {
		for (MonitoringDataRecord record : records) {
			
			// Date and time processing
			String dateTime = record.getMonitoringDate();
			if (dateTime != null) {
				final Matcher dateMatcher = MonitoringDataRecord.DATE_TIME_PATTERN.matcher(dateTime);
				if (dateMatcher.matches()) {
					String internationalDate = dateMatcher.group("internationalDate");
					String ukDate = dateMatcher.group("ukDate");
					
					// Default to international date if this was specified.
					String outputDate = internationalDate;
					// If date specified in UK format then we need to reverse this to produce an international date
					if (ukDate != null) {
						String[] dateParts = ukDate.split(MonitoringDataRecord.REGEX_DATE_SEPARATOR);
						ArrayUtils.reverse(dateParts);
						outputDate = StringUtils.join(dateParts, MonitoringDataRecord.DEFAULT_DATE_SEPARATOR);
					}

					String[] timeParts = { dateMatcher.group("hour"), dateMatcher.group("minute"), dateMatcher.group("second") };
					
					StringBuilder validDateString = new StringBuilder(outputDate);
					if (timeParts[0] != null) {
						validDateString.append("T");
						validDateString.append(StringUtils.join(timeParts, MonitoringDataRecord.DEFAULT_TIME_SEPARATOR));
					}
					record.setMonitoringDate(validDateString.toString());
				}
			}
			
		}
	}
	
	
	/**
	 * Write a list of {@link MonitoringDataRecord} entries to a {@link File}
	 * 
	 * @param records the {@link List} of {@link MonitoringDataRecord} to be written
	 * @param outputFile the output {@link File} to be written to
	 */
	private final void writeCSVFile(final List<MonitoringDataRecord> records, final File outputFile) {
		final MiscSettings settings = config.getMiscSettings();
		final Character csvDelimiter = settings.getCSVSeparatorCharacter();
		final Set<String> allHeadings = DataReturnsHeaders.getAllHeadings();
		final CSVWriterSettings csvWriterSettings = new CSVWriterSettings(csvDelimiter, new ArrayList<>(allHeadings));
		csvWriterSettings.setTrimWhitespace(true);
		final CSVWriter<MonitoringDataRecord> writer = new CSVWriter<>(MonitoringDataRecord.class, csvWriterSettings);

		try {
			// Output to a file in outputdir/validated with the same name as the uploaded file.
			try (final OutputStream fos = FileUtils.openOutputStream(outputFile)) {
				writer.write(records, fos);
			}
		} catch (IOException e) {
			throw new DRIOException(e, "Unable to write validated CSV file");
		}
	}
}
