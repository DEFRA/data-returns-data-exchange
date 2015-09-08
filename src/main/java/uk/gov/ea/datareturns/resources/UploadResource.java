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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.EmailSettings;
import uk.gov.ea.datareturns.domain.UploadError;
import uk.gov.ea.datareturns.domain.UploadResult;
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
	private static String SUCCESS = "SUCCESS";
	private static String VALIDATION_FAILURE = "VALIDATION_FAILURE";
	private static String SYSTEM_FAILURE = "SYSTEM_FAILURE";

	public UploadResource(DataExchangeConfiguration config)
	{
		this.config = config;
	}

	@POST
	@Path("/file-upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult uploadFile(@FormDataParam("userId") int userId, @FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail)
	{
		UploadResult result = new UploadResult();
		String uploadedFileLocation = makePath(makePath(".", "uploaded"), fileDetail.getFileName());

		LOGGER.debug("uploadedFileLocation = " + uploadedFileLocation);
		
		result.setFileName(fileDetail.getFileName());

		try
		{
			writeToFile(uploadedInputStream, uploadedFileLocation);

			String[] fields = extractEAIdentifiers(uploadedFileLocation);
			result.setEaId(fields[0]);
			result.setSiteName(fields[1]);
			result.setReturnType(fields[2]);

			String fileKey = UUID.randomUUID().toString();
			uploads.put(fileKey, uploadedFileLocation);

			result.setFileKey(fileKey);
			result.setOutcome(SUCCESS);
		} catch (Exception e)
		{
			result.setOutcome(SYSTEM_FAILURE);
			result.setOutcomeMessage(e.getMessage());
		}

		return result;
	}

	@POST
	@Path("/file-upload-validate")
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult validateFile(@FormParam("fileKey") String fileKey, @FormParam("eaId") String eaId, @FormParam("siteName") String siteName,
			@FormParam("returnType") String returnType)
	{
		UploadResult result = new UploadResult(fileKey, eaId, siteName, returnType);

		String uploadedFileLocation = uploads.get(fileKey);
		LOGGER.debug("uploadedFileLocation = " + uploadedFileLocation);
		
		result.setFileName(FilenameUtils.getName(uploadedFileLocation));
		
		validateUpload(uploadedFileLocation, result);

		return result;
	}

	@POST
	@Path("/file-upload-send")
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult sendFile(@FormParam("fileKey") String fileKey)
	{
		UploadResult result = new UploadResult();

		String uploadedFileLocation = uploads.get(fileKey);

		try
		{
			sendNotification(uploadedFileLocation);
			File f = new File(uploadedFileLocation);

			result.setOutcome(SUCCESS);
			result.setOutcomeMessage("sent '" + f.getName() + "' successfully to MonitorPro'");
		} catch (Exception e)
		{
			result.setOutcome(SYSTEM_FAILURE);
			result.setOutcomeMessage("Failed to send notification to MonitorPro");
		}

		return result;
	}

	private UploadResult validateUpload(String uploadedFileLocation, UploadResult result)
	{
		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		// Are all permits owned by this customer? - removed for sprint 5
		// UploadResult result = verifyUserPermits(uploadedFileLocation, userId);

		String schemaPath = makePath(makePath(".", "uploaded"), makeSchemaName(result.getReturnType()));

		if (!fileExists(schemaPath))
		{
			result.setOutcome(SYSTEM_FAILURE);
			result.setOutcomeMessage("Schema file '" + schemaPath +"' not found");

			return result;
		}

		// Validate contents
		List<FailMessage> errors = CsvValidator.validate(uploadedFileLocation, schemaPath, failFast, pathSubstitutions, true);
		LOGGER.debug("Validate done, total errors found = " + errors.size());

		// All good
		if (errors.size() == 0)
		{
			LOGGER.debug("File is VALID");
			result.setOutcome(SUCCESS);
		} else
		{
			if (errors.size() == 1 && errors.get(0).getMessage().startsWith("Unable to read file"))
			{
				result.setOutcome(SYSTEM_FAILURE);
				result.setOutcomeMessage("Uploaded file '" + FilenameUtils.getName(uploadedFileLocation) +"' not found");

				return result;
			}

			LOGGER.debug(VALIDATION_FAILURE);
			result.setFileName(FilenameUtils.getName(uploadedFileLocation));
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

		return result;
	}

	private boolean fileExists(String filePath)
	{
		File f = new File(filePath);

		return f.exists();
	}

	private String[] extractEAIdentifiers(String uploadedFileLocation)
	{
		File file = new File(uploadedFileLocation);
		String fileName = FilenameUtils.getName(uploadedFileLocation); 
		FileReader fr;
		int lineNo = 1;
		String[] fields = null;
		boolean dataFound = false;

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

					dataFound = true;

					break;
				}
			}

			br.close();
			fr.close();

		} catch (FileNotFoundException e)
		{
			throw new RuntimeException("Error opening file '" + fileName + "'");
		} catch (IOException e)
		{
			throw new RuntimeException("Error reading file '" + fileName + "'");
		}
		
		// Covers lots of different types of error
		if (!dataFound)
		{
			if(lineNo == 1)
			{
				throw new RuntimeException("Empty file '" + fileName + "'");
			}
			else
			{
				throw new RuntimeException("File '" + fileName + "' must contain at least 1 data row");
			}
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

		result.addErrors(errors);

		return result;
	}

	private boolean verifyUserPermit(int userId, String permitNo, String uniqueId)
	{
		// String found = dao.findByUserIdPermitNo(userId, permitNo);
		// TODO make call to db on both fields here but for now -
		// System.out.println(found);

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
