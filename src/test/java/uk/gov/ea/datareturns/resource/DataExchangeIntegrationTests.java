package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.DataExchangeHelper.generateUniqueFileKey;
import static uk.gov.ea.datareturns.resource.DataExchangeResource.APP_STATUS_SUCCESS;
import static uk.gov.ea.datareturns.resource.DataExchangeResource.APP_STATUS_SUCCESS_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.ApplicationException.EMPTY_FILE;
import static uk.gov.ea.datareturns.type.ApplicationException.FILE_KEY_MISMATCH;
import static uk.gov.ea.datareturns.type.ApplicationException.INSUFFICIENT_DATA;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_FILE_CONTENTS;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_FILE_TYPE;
import static uk.gov.ea.datareturns.type.SystemException.FILE_UNLOCATABLE;
import static uk.gov.ea.datareturns.type.SystemException.NOTIFICATION;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Rule;
import org.junit.Test;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.domain.DataExchangeResult;

import com.google.gson.Gson;

public class DataExchangeIntegrationTests
{
	public final static MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");

	public final static String TEST_FILES_PATH = "/testfiles";

	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_CSV_FAILURES = "failures.csv";
	public final static String FILE_CSV_EMPTY = "empty.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";
	public final static String FILE_NON_CSV = "invalid-type.txt";
	public final static String FILE_NON_TEXT = "binary.csv";

	public final static String URI = "http://localhost:%d/data-exchange/%s";

	public final static String STEP_UPLOAD = "upload";
	public final static String STEP_VALIDATE = "validate";
	public final static String STEP_COMPLETE = "complete";

	@Rule
	public final DropwizardAppRule<DataExchangeConfiguration> RULE = new DropwizardAppRule<>(App.class,
			ResourceHelpers.resourceFilePath("configuration_test.yml"));

	// UPLOAD STEP START
	@Test
	public void testEAIdentifierData()
	{
		final Client client = createUploadStepClient("test EA Identifier extraction");

		final Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
		assertThat(result.getFileKey()).isNotEmpty();
		assertThat(result.getEaId()).isNotEmpty();
		assertThat(result.getSiteName()).isNotEmpty();
		assertThat(result.getReturnType()).isNotEmpty();
	}

	@Test
	public void testInvalidFileType()
	{
		final Client client = createUploadStepClient("test Invalid File Type");

		final Response resp = performUploadStep(client, FILE_NON_CSV, MediaType.TEXT_PLAIN_TYPE);
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_FILE_TYPE.getAppStatusCode());
	}

	@Test
	public void testEmptyFileFailure()
	{
		final Client client = createUploadStepClient("test Empty File Failure");
		final Response resp = performUploadStep(client, FILE_CSV_EMPTY, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(EMPTY_FILE.getAppStatusCode());
	}

	@Test
	public void testInsufficientData()
	{
		final Client client = createUploadStepClient("test Insufficient Data in file");

		final Response resp = performUploadStep(client, FILE_CSV_INSUFFICIENT_DATA, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(INSUFFICIENT_DATA.getAppStatusCode());
	}

	@Test
	public void testInvalidContents()
	{
		final Client client = createUploadStepClient("test Non text File Contents");

		final Response resp = performUploadStep(client, FILE_NON_TEXT, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_FILE_CONTENTS.getAppStatusCode());
	}

	// UPLOAD STEP END

	// VALIDATE STEP START
	@Test
	public void testFileKeyMismatch()
	{
		final Client client = createValidateStepClient("test invalid File Key");

		final Response resp = performValidateStep(client, generateUniqueFileKey(), "invalid_return_type");
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(FILE_KEY_MISMATCH.getAppStatusCode());
	}

	@Test
	public void testMissingSchemaFile()
	{
		Client client = createUploadStepClient("test Missing Schema File 1/2");

		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = resp.readEntity(DataExchangeResult.class);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Missing Schema File 2/2");

		resp = performValidateStep(client, result.getFileKey(), "invalid_return_type");
		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

		final DataExchangeResult result2 = getResultFromResponse(resp);
		assertThat(result2.getAppStatusCode()).isEqualTo(FILE_UNLOCATABLE.getCode());
	}

	@Test
	public void testWithValidationErrors()
	{
		Client client = createUploadStepClient("test Validation Error(s) generated 1/2");

		Response resp = performUploadStep(client, FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = resp.readEntity(DataExchangeResult.class);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Missing Schema File 2/2");

		resp = performValidateStep(client, result.getFileKey(), result.getReturnType());
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS_WITH_ERRORS);
		assertThat(result.getErrors().size()).isNotEqualTo(0);
	}

	// VALIDATE STEP END

	// COMPLETE STEP START
	
	@Test
	// TODO fails if smtp server available
	public void testUserNotificationFailure()
	{
		Client client = createUploadStepClient("test Validation Error generation 1/3");

		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = resp.readEntity(DataExchangeResult.class);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Validation Error generation 2/3");

		resp = performValidateStep(client, result.getFileKey(), result.getReturnType());
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = resp.readEntity(DataExchangeResult.class);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createCompleteStepClient("test Validation Error generation 3/3");

		resp = performCompleteStep(client, result.getFileKey());
		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

		result = resp.readEntity(DataExchangeResult.class);
		assertThat(result.getAppStatusCode()).isEqualTo(NOTIFICATION.getCode());
	}

	// COMPLETE STEP START

	private Client createUploadStepClient(String testName)
	{
		final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
		configuration.setChunkedEncodingEnabled(false);

		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build(testName).register(MultiPartFeature.class);
		client.property(ClientProperties.READ_TIMEOUT, 10000);

		return client;
	}

	private Response performUploadStep(Client client, String testFileName, MediaType mediaType)
	{
		final FormDataMultiPart form = new FormDataMultiPart();
		final InputStream data = this.getClass().getResourceAsStream(makeFullPath(TEST_FILES_PATH, testFileName));
		final String uri = String.format(URI, RULE.getLocalPort(), STEP_UPLOAD);
		final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);

		form.bodyPart(fdp1);

		final Response resp = client.register(MultiPartFeature.class).target(uri).request()
				.post(Entity.entity(form, form.getMediaType()), Response.class);

		return resp;
	}

	private Client createValidateStepClient(String testName)
	{
		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).build(testName);

		client.property(ClientProperties.READ_TIMEOUT, 10000);

		return client;
	}

	private Response performValidateStep(Client client, String fileKey, String returnType)
	{
		final String uri = String.format(URI, RULE.getLocalPort(), STEP_VALIDATE);
		final Response resp = client.target(uri).queryParam("fileKey", fileKey).queryParam("returnType", returnType).request().get(Response.class);

		return resp;
	}

	private Client createCompleteStepClient(String testName)
	{
		final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
		configuration.setChunkedEncodingEnabled(false);

		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build(testName).register(MultiPartFeature.class);

		return client;
	}

	private Response performCompleteStep(Client client, String fileKey)
	{
		final FormDataMultiPart form = new FormDataMultiPart();
		final FormDataBodyPart fdp1 = new FormDataBodyPart("fileKey", fileKey);
		form.bodyPart(fdp1);
		final FormDataBodyPart fdp2 = new FormDataBodyPart("emailcc", "abc@abc.com");
		form.bodyPart(fdp2);

		final String uri = String.format(URI, RULE.getLocalPort(), STEP_COMPLETE);
		final Response resp = client.target(uri).request().post(Entity.entity(form, form.getMediaType()), Response.class);

		return resp;
	}

	private DataExchangeResult getResultFromResponse(Response resp)
	{
		final Gson gson = new Gson();

		return gson.fromJson(resp.readEntity(String.class), DataExchangeResult.class);
	}
}
