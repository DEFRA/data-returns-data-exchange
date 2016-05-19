package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.model.processors.FileCompletionProcessor;
import uk.gov.ea.datareturns.domain.model.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;

@Path("/data-exchange/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataExchangeResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

	private final ApplicationContext context;

	@Inject
	public DataExchangeResource(final ApplicationContext context) {
		this.context = context;
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
	@FilenameAuthorization
	public Response uploadFile(
			@FormDataParam("fileUpload") final InputStream is,
			@FormDataParam("fileUpload") final FormDataContentDisposition fileDetail) throws Exception {
		LOGGER.debug("/data-exchange/upload request received");

		// Define the client file name.  Make sure we strip path information to protect against naughty clients
		String clientFilename = "undefined.csv";
		if (fileDetail != null && fileDetail.getFileName() != null) {
			final File file = new File(fileDetail.getFileName());
			clientFilename = file.getName();
		}

		final FileUploadProcessor processor = this.context.getBean(FileUploadProcessor.class);
		processor.setClientFilename(clientFilename);
		processor.setInputStream(is);

		final DataExchangeResult result = processor.process();
		// Default response status
		Status responseStatus = Status.OK;
		if (result.getAppStatusCode() != -1) {
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
	 * @return {@link Response} object
	 * @throws Exception
	 */
	@POST
	@Path("/complete")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	@FilenameAuthorization
	public Response completeUpload(
			@NotEmpty @FormDataParam("fileKey") final String orgFileKey,
			@NotEmpty @FormDataParam("userEmail") final String userEmail,
			@NotEmpty @FormDataParam("orgFileName") final String orgFileName) throws Exception {
		LOGGER.debug("/data-exchange/complete request received");

		final FileCompletionProcessor processor = this.context.getBean(FileCompletionProcessor.class);
		processor.setOriginalFilename(orgFileName);
		processor.setStoredFileKey(orgFileKey);
		processor.setUserEmail(userEmail);

		final DataExchangeResult result = processor.process();
		return Response.status(Status.OK).entity(result).build();
	}
}