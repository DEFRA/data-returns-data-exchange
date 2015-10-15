package uk.gov.ea.datareturns.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;
import uk.gov.ea.datareturns.domain.UploadFileError;
import uk.gov.ea.datareturns.domain.UploadFileResult;
import uk.gov.ea.datareturns.exception.application.EmptyFileException;
import uk.gov.ea.datareturns.exception.application.FileKeyMismatchException;
import uk.gov.ea.datareturns.exception.application.InsufficientDataException;
import uk.gov.ea.datareturns.exception.application.InvalidFileContentsException;
import uk.gov.ea.datareturns.exception.application.InvalidFileTypeException;
import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;
import uk.gov.ea.datareturns.exception.system.NotificationException;
import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;

@Path("/submit-returns")
public class SubmitReturnsResource
{
	DataExchangeConfiguration config;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubmitReturnsResource.class);

	// TODO not sure yet how best to handle these
	public static int APP_STATUS_SUCCESS = 800;
	public static int APP_STATUS_SUCCESS_WITH_ERRORS = 801;

	private static String FILE_TYPE_CSV = "csv";
	private static String FILE_STORAGE_LOCATION = "schemas"; // TODO get this from somewhere?

	private Map<String, String> fileKeys;
	private Map<String, String> acceptableFileTypes;

	public SubmitReturnsResource(DataExchangeConfiguration config)
	{
		this.config = config;

		this.fileKeys = new HashMap<String, String>();

		this.acceptableFileTypes = new HashMap<String, String>();
		this.acceptableFileTypes.put(FILE_TYPE_CSV, "Comma Separated");
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		UploadFileResult result = new UploadFileResult();
		String fileLocation = makePath(makePath(".", FILE_STORAGE_LOCATION), fileDetail.getFileName());

		LOGGER.debug("fileLocation = " + fileLocation);

		result.setFileName(fileDetail.getFileName());

		// Save file to disk for now
		writeToFile(uploadedInputStream, fileLocation);

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

	@GET
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateFileUpload(@QueryParam("fileKey") String fileKey, @QueryParam("eaId") String eaId,
			@QueryParam("siteName") String siteName, @QueryParam("returnType") String returnType)
	{
		UploadFileResult result = new UploadFileResult(fileKey, eaId, siteName, returnType);

		String fileLocation = retrieveFileLocationByKey(fileKey);
		LOGGER.debug("fileLocation = " + fileLocation);

		result.setFileName(FilenameUtils.getName(fileLocation));

		performValidation(fileLocation, result);

		result.setAppStatusCode(result.getErrors().size() == 0 ? APP_STATUS_SUCCESS : APP_STATUS_SUCCESS_WITH_ERRORS);

		return Response.ok(result).build();
	}

	@POST
	@Path("/complete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response completeFileUpload(@FormDataParam("fileKey") String fileKey, @FormDataParam("emailcc") String userEmail)
	{
		UploadFileResult result = new UploadFileResult();

		String fileLocation = retrieveFileLocationByKey(fileKey);
		String fileName = FilenameUtils.getName(fileLocation);

		if (!userEmail.trim().isEmpty())
		{
			sendNotificationToUser(fileLocation, userEmail);
		}

		sendNotificationToMonitorPro(fileLocation);

		result.setOutcomeMessage("sent '" + fileName + "' successfully to MonitorPro'");

		result.setAppStatusCode(APP_STATUS_SUCCESS);

		return Response.ok(result).build();
	}

	private UploadFileResult performValidation(String fileLocation, UploadFileResult result)
	{
		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		String schemaLocation = makePath(makePath(".", FILE_STORAGE_LOCATION), makeSchemaName(result.getReturnType()));

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

				UploadFileError err = result.addError(mess.getMessage());

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

	private String retrieveFileLocationByKey(String fileKey)
	{
		String fileLocation = fileKeys.get(fileKey);

		if (fileLocation == null)
		{
			throw new FileKeyMismatchException("Unable to locate file using file key '" + fileKey + "'");
		}

		return fileLocation;
	}

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
			throw new FileUnlocatableException("Cannot locate file '" + fileName + "'");
		} catch (IOException e)
		{
			throw new FileReadException("Unable to read from file '" + fileName + "'");
		}

		// Non-text file
		// TODO needs better way than just checking field count 
		if (fields == null || fields.length < 3)
		{
			throw new InvalidFileContentsException("File '" + fileName + "' contains invalid contents");
		}

		return fields;
	}

	private String makeSchemaName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + ".csvs";
	}

	private String makePath(String leftPart, String rightPart)
	{
		return leftPart + File.separator + rightPart;
	}

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
			throw new NotificationException("Failed to send email to '" + userEmail + "'");
		}
		catch (Exception e2)
		{
			throw new NotificationException("Failed to send email to '" + userEmail + "'");
		}
	}

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
			throw new NotificationException("Failed to send email to MonitorPro");
		}
		catch (Exception e2)
		{
			throw new NotificationException("Failed to send email to MonitorPro");
		}
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String filePath)
	{
		String fileName = FilenameUtils.getName(filePath);
		int read;
		final int BUFFER_LENGTH = 1024;
		final byte[] buffer = new byte[BUFFER_LENGTH];

		try
		{
			OutputStream out = new FileOutputStream(new File(filePath));

			while ((read = uploadedInputStream.read(buffer)) != -1)
			{
				out.write(buffer, 0, read);
			}

			out.flush();
			out.close();
		} catch (FileNotFoundException e1)
		{
			throw new FileSaveException("Unable to save file to '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException("Unable to read from file '" + fileName + "'");
		}
	}

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

	private String generateUniqueFileKey()
	{
		return UUID.randomUUID().toString();
	}

	private boolean fileContainsMinRows(String filePath, int i)
	{
		File file = new File(filePath);
		String fileName = FilenameUtils.getName(filePath);
		FileReader fr;
		int lineNo = 1;
		boolean minRowsFound = false;

		try
		{
			fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null)
			{
				if (lineNo++ == 2)
				{
					minRowsFound = true;

					break;
				}
			}

			br.close();
			fr.close();

		} catch (FileNotFoundException e1)
		{
			throw new FileUnlocatableException("Cannot locate file '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException("Unable to read from file '" + fileName + "'");
		}

		if (!minRowsFound)
		{
			throw new InsufficientDataException("File '" + fileName + "' does not contain minimum data");
		}

		return minRowsFound;
	}

	private String getFileType(String filePath)
	{
		String fileType = null;
		int i = filePath.lastIndexOf('.');

		if (i > 0)
		{
			fileType = filePath.substring(i + 1).toLowerCase();
		}

		return fileType;
	}
}
