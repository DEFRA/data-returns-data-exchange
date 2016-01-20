package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullFilePath;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.generateFileKey;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.saveFile;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.deserializeFromXML;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.transformToString;
import static uk.gov.ea.datareturns.type.AppStatusCode.APP_STATUS_FAILED_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.AppStatusCode.APP_STATUS_SUCCESS;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;
import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.convert.ConvertCSVToXML;
import uk.gov.ea.datareturns.dao.PermitDAO;
import uk.gov.ea.datareturns.domain.result.CompleteResult;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.GeneralResult;
import uk.gov.ea.datareturns.domain.result.UploadResult;
import uk.gov.ea.datareturns.domain.result.ValidationResult;
import uk.gov.ea.datareturns.exception.application.FileKeyMismatchException;
import uk.gov.ea.datareturns.exception.application.MultiplePermitsException;
import uk.gov.ea.datareturns.exception.application.PermitNotFoundException;
import uk.gov.ea.datareturns.exception.application.UnsupportedFileTypeException;
import uk.gov.ea.datareturns.exception.system.NotificationException;
import uk.gov.ea.datareturns.type.FileType;
import uk.gov.ea.datareturns.validate.Validate;
import uk.gov.ea.datareturns.validate.ValidateXML;

import com.codahale.metrics.annotation.Timed;

@Path("/data-exchange/")
public class DataExchangeResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

	private DataExchangeConfiguration config;
	private PermitDAO permitDAO;

	private final String XSLT_UNIQUE_IDENTIFIERS = "get_unique_identifiers.xslt";
	private final String XSLT_PERMIT_NOS = "get_unique_permit_nos.xslt";

	// TODO things that could/should be held in database?
	private final String CORE_SCHEMA_FILE = "data-returns-core-schema.xsd";
	private final String CORE_TRANSLATIONS_FILE = "data-returns-core-translations.xml";
	private final String PERMIT_NO_FIELD_ID = "EA_ID";
	private final String[] uniqueIdentifierIds = new String[]
	{ "EA_ID", "Site_Name", "Rtn_Type" };

	private Map<String, String> fileKeys;

	public DataExchangeResource(DataExchangeConfiguration config, PermitDAO permitDAO)
	{
		this.config = config;
		this.permitDAO = permitDAO;
		this.fileKeys = new HashMap<String, String>();
	}

	/**
	 * REST method to handle Returns file upload.
	 *      1. Validate upload file
	 * 		2. Save upload file
	 * 		3. Convert upload file to XML
	 * 		4. Apply Permit number validations
	 * 		5. Apply File content validations
	 * @param is
	 * @param fileDetail
	 * @return JSON object
	 */
	@POST
	@Path("/upload")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	@Timed
	public Response uploadFile(@FormDataParam("fileUpload") InputStream is, @FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		MiscSettings settings = config.getMiscSettings();
		String uploadedFile = makeFullFilePath(settings.getUploadedLocation(), fileDetail.getFileName());

		// 1. Validate upload file
		validateUploadFile(uploadedFile);

		// 2. Save upload file
		saveFile(is, uploadedFile);

		// 3. Convert uploaded file to XML
		ConvertCSVToXML converter = new ConvertCSVToXML(settings.getCsvSeparator(), uploadedFile, settings.getOutputLocation());
		converter.convert();

		String workingFile = converter.getConvertedFile();

		// 4. Apply Permit number validations
		validatePermitNo(workingFile);

		// 5. Apply File content validations
		ValidationResult validateResult = validateFile(workingFile);

		// Whoohoo! prepare success/failure response

		UploadResult uploadResult = new UploadResult(fileDetail.getFileName());
		DataExchangeResult result = new DataExchangeResult(uploadResult);

		if (validateResult.isValid())
		{
			// TODO needs determining exactly what the front-end needs
			GeneralResult generalResult = getUniqueIdentifiers(workingFile);
			result.setGeneralResult(generalResult);

			// Pointer to the file
			uploadResult.setFileKey(generateFileKey());
			result.setAppStatusCode(APP_STATUS_SUCCESS.getAppStatusCode());

			// TODO store fileKey in database/redis?
			fileKeys.put(uploadResult.getFileKey(), uploadedFile);
		} else
		{
			result.setValidationResult(validateResult);
			result.setAppStatusCode(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());
		}

		return Response.ok(result).build();
	}

	// TODO validate entity would be better?
	@POST
	@Path("/complete")
	@Produces(APPLICATION_JSON)
	@Timed
	public Response completeFileUpload(@NotEmpty @FormDataParam("fileKey") String fileKey, @NotEmpty @FormDataParam("userEmail") String userEmail)
	{
		CompleteResult completeResult = new CompleteResult();
		DataExchangeResult result = new DataExchangeResult(completeResult);

		completeResult.setFileKey(fileKey);
		completeResult.setUserEmail(userEmail);

		String fileLocation = retrieveFileLocationByKey(completeResult.getFileKey());

		sendNotificationToMonitorPro(fileLocation);

		result.setAppStatusCode(APP_STATUS_SUCCESS.getAppStatusCode());

		return Response.ok(result).build();
	}

	/**
	 * Basic file validation
	 * @param uploadedFile
	 */
	private void validateUploadFile(String uploadedFile)
	{
		String fileType = getConverterType(uploadedFile);

		// Release 1 must be csv
		if (!fileType.equalsIgnoreCase(FileType.CSV.getFileType()))
		{
			throw new UnsupportedFileTypeException("Unsupported file type '" + fileType + "'");
		}
	}

	/**
	 * Determines the converter type using file name extension
	 * @param fileName
	 * @return
	 */
	private String getConverterType(String fileName)
	{
		return FilenameUtils.getExtension(fileName);
	}

	/**
	 * Apply File content validations
	 * @param workingFile
	 * @return
	 */
	private ValidationResult validateFile(String workingFile)
	{
		MiscSettings settings = config.getMiscSettings();

		// Validate XML file
		String schemaFile = makeFullFilePath(settings.getSchemaLocation(), CORE_SCHEMA_FILE);
		String xsltLocation = settings.getXsltLocation();
		String translationsFile = makeFullFilePath(settings.getXmlLocation(), CORE_TRANSLATIONS_FILE);
		Validate validate = new ValidateXML(schemaFile, workingFile, translationsFile, xsltLocation);

		return validate.validate();
	}

	/**
	 * Apply Permit number validations
	 * @param workingFile
	 * @return
	 */
	private GeneralResult validatePermitNo(String workingFile)
	{
		LOGGER.debug("Performing Permit No validations");

		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeName", PERMIT_NO_FIELD_ID);

		String result = transformToString(workingFile, makeFullFilePath(config.getMiscSettings().getXsltLocation(), XSLT_PERMIT_NOS), params);

		GeneralResult generalResult = deserializeFromXML(result, GeneralResult.class);

		// Release 1 - single permit only
		if (generalResult.getResultCount() > 1)
		{
			throw new MultiplePermitsException("Multiple Permits found in file '" + FilenameUtils.getName(workingFile) + "'");
		}

		String permitNo = generalResult.getSingleResultValue();

		if (permitDAO.findByPermitNumber(permitNo) == null)
		{
			throw new PermitNotFoundException("Permit no '" + permitNo + "' not found");
		}

		LOGGER.debug("Permit is valid'");

		return generalResult;
	}

	/**
	 * Retrieve stored file location by key
	 * @param fileKey
	 * @return
	 */
	private String retrieveFileLocationByKey(String fileKey)
	{
		String fileLocation = fileKeys.get(fileKey);

		if (fileLocation == null)
		{
			throw new FileKeyMismatchException("Unable to locate file using file key '" + fileKey + "'");
		}

		return fileLocation;
	}

	/**
	 * Extract values that identify a unique EA return type
	 * @param fileLocation
	 * @param ids
	 * @return
	 */
	private GeneralResult getUniqueIdentifiers(String fileLocation)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeNames", String.join(",", uniqueIdentifierIds));

		String result = transformToString(fileLocation, makeFullFilePath(config.getMiscSettings().getXsltLocation(), XSLT_UNIQUE_IDENTIFIERS), params);

		return deserializeFromXML(result, GeneralResult.class);
	}

	/**
	 * Sends and Email to MonitorPro (also know as EMMA) with uploaded file (un-modified) as an attachment
	 * @param attachmentLocation
	 */
	private void sendNotificationToMonitorPro(String attachmentLocation)
	{
		EmailSettings settings = this.config.getEmailsettings();
		MultiPartEmail email = new MultiPartEmail();

		try
		{
			// TODO pass this in?
			email.setSubject("Landfill Gas Monitoring");

			email.setHostName(settings.getHost());
			email.setSmtpPort(settings.getPort());
			email.addTo(settings.getEmailTo());
			email.setFrom(settings.getEmailFrom());
			email.setMsg(settings.getBodyMessage());
			email.setStartTLSEnabled(settings.getTls());
			email.setAuthentication(settings.getUser(), settings.getPassword());

			EmailAttachment attachment = new EmailAttachment();
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Data Returns File Upload");
			attachment.setName("Environment Agency");

			File fileAttachment = new File(attachmentLocation);
			attachment.setPath(fileAttachment.getAbsolutePath());

			email.attach(fileAttachment);

			LOGGER.debug("Sending email to MonitorPro - ");
			LOGGER.debug("  host - " + settings.getHost());
			LOGGER.debug("  port - " + settings.getPort());
			LOGGER.debug("  emailTo - " + settings.getEmailTo());
			LOGGER.debug("  emailFrom - " + settings.getEmailFrom());
			LOGGER.debug("  user - " + settings.getUser());
			LOGGER.debug("  password - " + settings.getPassword());
			LOGGER.debug("  tls - " + settings.getTls());
			LOGGER.debug("  bodyMessage - " + settings.getBodyMessage());
			email.send();
		} catch (EmailException e1)
		{
			throw new NotificationException(e1, "Failed to send email to MonitorPro");
		} catch (Exception e2)
		{
			throw new NotificationException(e2, "Failed to send email to MonitorPro");
		}
	}
}
