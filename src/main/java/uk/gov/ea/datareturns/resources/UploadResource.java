package uk.gov.ea.datareturns.resources;

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

//TODO validation would probably need be done in 2 stages

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource
{
	DataExchangeConfiguration config;
	MyDAO dao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
	private static Map<String, String> uploads = new HashMap<String, String>();

	public UploadResource(DataExchangeConfiguration config)
	{
		this.config = config;
	}

	public UploadResource(DataExchangeConfiguration config, MyDAO dao)
	{
		this.config = config;
		this.dao = dao;
	}

	@POST
	@Path("/file-upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult uploadFile(@FormDataParam("userId") int userId, @FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		String uploadedFileLocation = makePath(makePath(".", "uploaded"), fileDetail.getFileName());
		String fileSchema = makePath(makePath(".", "uploaded"), "data-schema.csvs");

		LOGGER.debug("uploadedFileLocation = " + uploadedFileLocation);
		LOGGER.debug("fileSchema = " + fileSchema);

		saveFile(uploadedInputStream, uploadedFileLocation);

		UploadResult result = validateUpload(uploadedFileLocation, userId);

		return result;
	}

	private void saveFile(InputStream uploadedInputStream, String uploadedFileLocation)
	{
		try
		{
			LOGGER.debug("Attempting to write to '" + uploadedFileLocation + "'");
			writeToFile(uploadedInputStream, uploadedFileLocation);
			LOGGER.debug("File written successfully to '" + uploadedFileLocation + "'");
		} catch (IOException e)
		{
			LOGGER.debug("Failed writing to '" + uploadedFileLocation + "'");
			e.printStackTrace();
		}
	}

	private UploadResult validateUpload(String uploadedFileLocation, int userId)
	{
		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		// Are all permits owned by this customer?
		UploadResult result = verifyUserPermits(uploadedFileLocation, userId);

		// Yes they are, validate content
		if (result.getErrors().size() == 0)
		{
			String schemaPath = makePath(makePath(".", "uploaded"), makeSchemaName(result.getReturnType()));

			// Validate contents
			List<FailMessage> errors = CsvValidator.validate(uploadedFileLocation, schemaPath, failFast, pathSubstitutions, true);
			LOGGER.debug("Validate done, total errors found = " + errors.size());

			// All good
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
					LOGGER.debug("Error = " + mess.getMessage());
					
					UploadError err = result.addError(mess.getMessage());
					
					LOGGER.debug("  reason = " + err.getReason());
					LOGGER.debug("  lineNo; = " + err.getLineNo());
					LOGGER.debug("  columnName; = " + err.getColumnName());
					LOGGER.debug("  errValue; = " + err.getErrValue());
					LOGGER.debug("  meaningfulReason = " + err.getMeaningfulReason());
					LOGGER.debug("  helpfulExample = " + err.getHelpfulExample());
				}
			}
		}

		return result;
	}

	private String makeSchemaName(String returnType)
	{
		return returnType.toLowerCase().replace(" ", "_") + ".csvs";
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

	private UploadResult verifyUserPermits(String uploadedFileLocation, int userId)
	{
		UploadResult result = new UploadResult();
		List<UploadError> errors = new ArrayList<UploadError>();
		File file = new File(uploadedFileLocation);
		FileReader fr;
		int lineNo = 0;
		String returnType = null;
		String[] cols = null;

		// TODO all should be be configurable

		try
		{
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null)
			{
				if (lineNo++ == 0)
				{
					cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				} else
				{
					String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

					// Only needs to do this once as long as file contains same
					// return type?
					returnType = fields[2];

					String uniqueId = makePermitIdentifier(fields[0], returnType, " - ");

					if (!verifyUserPermit(userId, fields[0], uniqueId))
					{
						errors.add(new UploadError("notReg", Integer.toString(lineNo), cols[0], fields[0]));
					}
				}
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		result.setReturnType(returnType);
		result.addErrors(errors);

		return result;
	}

	private boolean verifyUserPermit(int userId, String permitNo, String uniqueId)
	{
//		String found = dao.findByUserIdPermitNo(userId, permitNo);
		// TODO make call to db on both fields here but for now -

	//	System.out.println(found);
		
		Map<String, String> userPermits = new HashMap<String, String>();
		userPermits.put(makePermitIdentifier("1 - AB1234Z1", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("2 - AB1234Z2", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("1 - AB1234Z3", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("2 - AB1234Z4", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("1 - AB1234Z5", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("2 - AB1234Z6", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("1 - AB1234Z7", "Landfill Gas Monitoring", " - "), null);
		userPermits.put(makePermitIdentifier("2 - AB1234Z8", "Landfill Gas Monitoring", " - "), null);

		return userPermits.containsKey(makePermitIdentifier(Integer.toString(userId), uniqueId, " - "));
	}

	private String makePermitIdentifier(String permitNo, String returnType, String sep)
	{
		return permitNo + sep + returnType;
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
