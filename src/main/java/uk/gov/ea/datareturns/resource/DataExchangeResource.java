package uk.gov.ea.datareturns.resource;

import static uk.gov.ea.datareturns.helper.CommonHelper.getFileType;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.generateUniqueFileKey;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.makeSchemaName;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.fileContainsMinRows;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.saveReturnsFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;
import uk.gov.ea.datareturns.dao.PermitDAO;
import uk.gov.ea.datareturns.domain.DataExchangeError;
import uk.gov.ea.datareturns.domain.DataExchangeResult;
import uk.gov.ea.datareturns.exception.application.EmptyFileException;
import uk.gov.ea.datareturns.exception.application.FileKeyMismatchException;
import uk.gov.ea.datareturns.exception.application.InsufficientDataException;
import uk.gov.ea.datareturns.exception.application.InvalidFileContentsException;
import uk.gov.ea.datareturns.exception.application.InvalidFileTypeException;
import uk.gov.ea.datareturns.exception.application.PermitNotFoundException;
import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;
import uk.gov.ea.datareturns.exception.system.NotificationException;
import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;

import com.codahale.metrics.annotation.Timed;

// TODO gradually moving code to helpers to simplify unit testing - may end up in 'proper' classes eventually 
@Path("/data-exchange/")
public class DataExchangeResource
{
	private DataExchangeConfiguration config;
	private PermitDAO permitDAO;

	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

	// TODO not sure yet how best to handle these
	public static int APP_STATUS_SUCCESS = 800;
	public static int APP_STATUS_SUCCESS_WITH_ERRORS = 801;

	private static String FILE_TYPE_CSV = "csv";

	private Map<String, String> fileKeys;
	private Map<String, String> acceptableFileTypes;

	public DataExchangeResource(DataExchangeConfiguration config, PermitDAO permitDAO)
	{
		this.config = config;
		this.permitDAO = permitDAO;
		this.fileKeys = new HashMap<String, String>();
		this.acceptableFileTypes = new HashMap<String, String>();
		this.acceptableFileTypes.put(FILE_TYPE_CSV, "Comma Separated");
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response uploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		DataExchangeResult result = new DataExchangeResult();
		String fileLocation = makeFullPath(this.config.getMiscSettings().getFileUploadLocation(), fileDetail.getFileName());

		LOGGER.debug("fileLocation = " + fileLocation);

		result.setFileName(fileDetail.getFileName());

		saveReturnsFile(uploadedInputStream, fileLocation);

		// Validate file (as much as possible) ready for "validate" step
		uploadStepValidation(fileLocation);

		// TODO configurable somewhere?
		String[] fields = extractEAIdentifiers(fileLocation);
		String fileKey = generateUniqueFileKey();

		result.setFileKey(fileKey);
		result.setEaId(fields[0]);
		result.setSiteName(fields[1]);
		result.setReturnType(fields[2]);

		// TODO where is best place to hold these?
		fileKeys.put(fileKey, fileLocation);

		result.setAppStatusCode(APP_STATUS_SUCCESS);

		return Response.ok(result).build();
	}

	// TODO validate entity would be better
	@GET
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response validateFileUpload(@NotEmpty @QueryParam("fileKey") String fileKey, @NotEmpty @QueryParam("eaId") String eaId,
			@NotEmpty @QueryParam("siteName") String siteName, @NotEmpty @QueryParam("returnType") String returnType)
	{
		DataExchangeResult result = new DataExchangeResult(fileKey, eaId, siteName, returnType);

		String fileLocation = retrieveFileLocationByKey(result.getFileKey());
		LOGGER.debug("fileLocation = " + fileLocation);

		// TODO single permit number check (for now)
		if (!permitNoExists(eaId))
		{
			throw new PermitNotFoundException("Permit no '" + eaId + "' not found");
		}

		result.setFileName(FilenameUtils.getName(fileLocation));

		performContentValidation(fileLocation, result);

		result.setAppStatusCode(result.getErrors().size() == 0 ? APP_STATUS_SUCCESS : APP_STATUS_SUCCESS_WITH_ERRORS);

		return Response.ok(result).build();
	}

	// TODO validate entity would be better
	@POST
	@Path("/complete")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response completeFileUpload(@NotEmpty @FormDataParam("fileKey") String fileKey,
			@DefaultValue("") @FormDataParam("userEmail") String userEmail)
	{
		DataExchangeResult result = new DataExchangeResult(fileKey, userEmail);

		String fileLocation = retrieveFileLocationByKey(result.getFileKey());

		if (result.isSendUserEmail())
		{
			sendNotificationToUser(fileLocation, result.getUserEmail());
		}

		sendNotificationToMonitorPro(fileLocation);

		result.setAppStatusCode(APP_STATUS_SUCCESS);

		return Response.ok(result).build();
	}

	// TODO try to move all methods code to helpers
	private boolean permitNoExists(String permitNumber)
	{
		return permitDAO.findByPermitNumber(permitNumber) != null;
	}

	private DataExchangeResult performContentValidation(String fileLocation, DataExchangeResult result)
	{
		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		String schemaLocation = makeFullPath(this.config.getMiscSettings().getSchemaFileLocation(), makeSchemaName(result.getReturnType()));

		// Validate contents
		List<FailMessage> errors = CsvValidator.validate(fileLocation, schemaLocation, failFast, pathSubstitutions, true);
		LOGGER.debug("Validate done, total errors found = " + errors.size());

		if (errors.size() > 0)
		{
			if (errors.size() == 1 && errors.get(0).getMessage().startsWith("Unable to read file"))
			{
				throw new FileUnlocatableException(errors.get(0).getMessage());
			}

			LOGGER.debug("Validation failed");
			result.setFileName(FilenameUtils.getName(fileLocation));

			for (FailMessage mess : errors)
			{
				LOGGER.debug("Error = " + mess.getMessage());

				DataExchangeError err = result.addError(mess.getMessage());

				LOGGER.debug("  reason = " + err.getReason());
				LOGGER.debug("  lineNo; = " + err.getLineNo());
				LOGGER.debug("  columnName; = " + err.getColumnName());
				LOGGER.debug("  errValue; = " + err.getErrValue());
				LOGGER.debug("  meaningfulReason = " + err.getMeaningfulReason());
				LOGGER.debug("  helpfulExample = " + err.getHelpfulExample());
			}
		}

		return result;
	}

	/**
	 * Retrieves the stored file location from the supplied file key
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
	 * Extracts EA Identifier field(s) from stored data file
	 * Just for now, these are 1st 3 field values on row 2
	 * @param fileLocation
	 * @return
	 */
	private String[] extractEAIdentifiers(String fileLocation)
	{
		File file = new File(fileLocation);
		String fileName = FilenameUtils.getName(fileLocation);
		FileReader fr;
		int lineNo = 1;
		String[] fields = null;

		try
		{
			fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null)
			{
				// Grab 1st 3 data fields used on verification page. Eventually will have multiple permits, only 1 needed for alpha
				if (lineNo++ == 2)
				{
					fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

					break;
				}
			}

			br.close();
			fr.close();

		} catch (FileNotFoundException e)
		{
			throw new FileUnlocatableException(e, "Cannot locate file '" + fileName + "'");
		} catch (IOException e)
		{
			throw new FileReadException(e, "Unable to read from file '" + fileName + "'");
		}

		// Non-text file
		// TODO needs better way than just checking field count 
		if (fields == null || fields.length < 3)
		{
			throw new InvalidFileContentsException("File '" + fileName + "' contains invalid contents");
		}

		return fields;
	}

	// TODO refactor with sendNotificationToMonitorPro()
	/**
	 * Sends and Email to the supplied email address with uploaded file (un-modified) as an attachment
	 * @param attachmentLocation
	 * @param userEmail
	 */
	private void sendNotificationToUser(String attachmentLocation, String userEmail)
	{
		EmailSettings settings = this.config.getEmailsettings();
		String fileName = FilenameUtils.getName(attachmentLocation);
		HtmlEmail email = new HtmlEmail();

		try
		{
			email.setSubject("File '" + fileName + "' has been successfully submitted to the Environment Agency");

			email.setHostName(settings.getHost());
			email.setSmtpPort(settings.getPort());
			email.addTo(userEmail);
			email.setFrom(settings.getEmailFrom());
			email.setMsg("<html><p>{username} has submitted file '" + fileName + "' to the Environment Agency</p></html>");
			email.setStartTLSEnabled(settings.getTls());
			email.setAuthentication(settings.getUser(), settings.getPassword());

			LOGGER.debug("Sending email to user - ");
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
			throw new NotificationException(e1, "Failed to send email to '" + userEmail + "'");
		} catch (Exception e2)
		{
			throw new NotificationException(e2, "Failed to send email to '" + userEmail + "'");
		}
	}

	// TODO refactor with sendNotificationToUser()
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

	/**
	 * Some pre-content file validation checks before handing over to CSV validation libary
	 * @param filePath
	 */
	private void uploadStepValidation(String filePath)
	{
		String fileName = FilenameUtils.getName(filePath);

		// Reject if extension is not csv
		String fileType = getFileType(filePath);
		if (fileType == null || !this.acceptableFileTypes.containsKey(fileType))
		{
			throw new InvalidFileTypeException("File '" + fileName + "' must be CSV");
		}

		// Reject if file is empty
		File f = new File(filePath);
		if (f.length() == 0)
		{
			throw new EmptyFileException("File '" + fileName + "' cannot be empty");
		}

		// Reject if file does not contain at least 2 rows (assumed to be header & data rows)
		if (!fileContainsMinRows(filePath, 2))
		{
			throw new InsufficientDataException("File '" + fileName + "' must contain a header and at least 1 data row");
		}
	}
}
