package uk.gov.ea.datareturns.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource
{
	DataExchangeConfiguration config;

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
	private static Map<String, String> uploads = new HashMap<String, String>();

	public UploadResource(DataExchangeConfiguration configuration)
	{
		this.config = configuration;
	}

	@POST
	@Path("/file-upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult uploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		String uploadedFileLocation = makePath(makePath(".", "uploaded"), fileDetail.getFileName());
		String fileSchema = makePath(makePath(".", "uploaded"), "data-schema.csvs");

		LOGGER.debug("uploadedFileLocation = " + uploadedFileLocation);
		LOGGER.debug("fileSchema = " + fileSchema);

		try
		{
			LOGGER.debug("Attempting to write to '" + uploadedFileLocation + "'");
			writeToFile(uploadedInputStream, uploadedFileLocation);
			LOGGER.debug("File written successfully to '" + uploadedFileLocation + "'");
		} catch (IOException e)
		{
			LOGGER.debug("Failed writing to '" + uploadedFileLocation + "'");
			e.printStackTrace();
			return null;
		}

		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		String currentDir = null;
		try
		{
			currentDir = new java.io.File(".").getCanonicalPath();
			LOGGER.debug("currentDir = '" + currentDir + "'");
		} catch (IOException e)
		{
			LOGGER.debug("Failed getting currunt dir");
			e.printStackTrace();
		}

		List<FailMessage> errors = CsvValidator.validate(uploadedFileLocation, fileSchema, failFast, pathSubstitutions, true);
		LOGGER.debug("Validate done, total errors found = " + errors.size());

		UploadResult result = new UploadResult();

		if (errors.size() == 0)
		{
			LOGGER.debug("File is VALID");
			result.setOutcome("File uploaded to : " + uploadedFileLocation);

			String key = UUID.randomUUID().toString();
			result.setKey(key);
			uploads.put(key, uploadedFileLocation);
		} else
		{
			LOGGER.debug("File is INVALID");
			result.setOutcome("File NOT uploaded : ");

			for (FailMessage mess : errors)
			{
				LOGGER.debug("Error + " + mess.getMessage());
				result.addError(mess.getMessage());
			}
		}

		return result;
	}

	private String makePath(String leftPart, String rightPart)
	{
		return leftPart + File.separator + rightPart;
	}

	@POST
	@Path("/file-upload-send")
	@Produces(MediaType.APPLICATION_JSON)
	public SendResult sendFile(@FormParam("key") String fileKey)
	{
		SendResult result = new SendResult();

		String uploadedFileLocation = uploads.get(fileKey);

		try
		{
			sendNotification(uploadedFileLocation);
			File f = new File(uploadedFileLocation);
			result.setMessage("sent '" + f.getName() + "' successfully to MonitorPro'");
		} catch (EmailException e)
		{
			result.setMessage("Failed to send notification to MonitorPro'");
			e.printStackTrace();
		}

		return result;
	}

	private void sendNotification(String attachmentLocation) throws EmailException
	{
		EmailSettings settings = this.config.getEmailsettings();

		MultiPartEmail email = new MultiPartEmail();

		// Read from file or lookup?
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

		LOGGER.debug("Sending email - ");
		LOGGER.debug("  host - " + settings.getHost());
		LOGGER.debug("  port - " + settings.getPort());
		LOGGER.debug("  emailTo - " + settings.getEmailTo());
		LOGGER.debug("  emailFrom - " + settings.getEmailFrom());
		LOGGER.debug("  user - " + settings.getUser());
		LOGGER.debug("  password - " + settings.getPassword());
		LOGGER.debug("  tls - " + settings.getTls());
		LOGGER.debug("  bodyMessage - " + settings.getBodyMessage());

		email.send();
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException
	{
		int read;
		final int BUFFER_LENGTH = 1024;
		final byte[] buffer = new byte[BUFFER_LENGTH];
		OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
		while ((read = uploadedInputStream.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
		out.flush();
		out.close();
	}
}
