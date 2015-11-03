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
import static uk.gov.ea.datareturns.type.SystemException.UNPROCESSABLE_ENTITY;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.domain.DataExchangeResult;

import com.google.gson.Gson;

/**
 * Integration test class for the DataExchangeResource REST service.
 * Uses DropwizardAppRule so real HTTP requests are fired at the interface (using grizzly server).
 * 
 * The tests are aimed mainly at verifying exceptions thrown from this service which are split in to -
 *     System - returns a standard HTML error code.
 *     Application - returns a standard HTML error code + an application specific status code to help identify what went wrong.
 * 
 * TODO may/may not implement testInvalidUploadInteface() & testInvalidCompleteInteface() tests as should be tested with e2e tests
 * TODO not every single exception is tested - not sure if they ever will as e2e test should provide full coverage
 * TODO could refactor/reduce tests as many tests retest same code BUT too much is better so not a priority
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceIntegrationTests
{
	public final static MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");

	public final static String TEST_FILES_PATH = "/testfiles";
	public final static String UPLOADED_PATH = "./uploaded";

	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_PERMIT_NOT_FOUND = "permit-not-found.csv";
	public final static String FILE_CSV_FAILURES = "failures.csv";
	public final static String FILE_CSV_EMPTY = "empty.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";
	public final static String FILE_NON_CSV = "invalid-type.txt";
	public final static String FILE_NON_TEXT = "binary.csv";

	public final static String FILE_CONFIG = "configuration_test.yml";

	public final static String URI = "http://localhost:%d/data-exchange/%s";

	public final static String STEP_UPLOAD = "upload";
	public final static String STEP_VALIDATE = "validate";
	public final static String STEP_COMPLETE = "complete";

	@Rule
	public final DropwizardAppRule<DataExchangeConfiguration> RULE = new DropwizardAppRule<>(App.class, ResourceHelpers.resourceFilePath(FILE_CONFIG));

	@BeforeClass
	public static void setUp()
	{
		// Safely delete ONLY known files before tests 
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_CSV_SUCCESS)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_PERMIT_NOT_FOUND)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_CSV_FAILURES)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_CSV_EMPTY)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_CSV_INSUFFICIENT_DATA)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_NON_CSV)));
		FileUtils.deleteQuietly(new File(makeFullPath(UPLOADED_PATH, FILE_NON_TEXT)));
	}

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

	// TODO may/may not implement
	//	@Test
	//	public void testInvalidUploadInteface()

	@Test
	public void testInvalidValidateInteface()
	{
		final Client client = createValidateStepClient("test invalid Validate Inteface ");

		final Response resp = performValidateStep(client);
		assertThat(resp.getStatus()).isEqualTo(UNPROCESSABLE_ENTITY.getCode());

		// TODO Should check more specific error message? 
	}

	@Test
	public void testFileKeyMismatch()
	{
		final Client client = createValidateStepClient("test invalid File Key");

		final Response resp = performValidateStep(client, generateUniqueFileKey(), "any_ea_id", "any_site_name", "invalid_return_type");
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

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Missing Schema File 2/2");

		resp = performValidateStep(client, result.getFileKey(), result.getEaId(), result.getSiteName(), "invalid_return_type");
		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(FILE_UNLOCATABLE.getCode());
	}

	@Test
	public void testPermitNumberFound()
	{
		Client client = createUploadStepClient("test Permit No Found 1/2");

		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Permit No Found 2/2");

		resp = performValidateStep(client, result.getFileKey(), result.getEaId(), result.getSiteName(), result.getReturnType());
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
	}

	@Test
	public void testPermitNumberNotFound()
	{
		Client client = createUploadStepClient("test Permit No Not Found 1/2");

		Response resp = performUploadStep(client, FILE_PERMIT_NOT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Permit No Not Found 2/2");

		resp = performValidateStep(client, result.getFileKey(), result.getEaId(), result.getSiteName(), result.getReturnType());
		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testWithValidationErrors()
	{
		Client client = createUploadStepClient("test Validation Error(s) generated 1/2");

		Response resp = performUploadStep(client, FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Validation Error(s) generated 2/2");

		resp = performValidateStep(client, result.getFileKey(), result.getEaId(), result.getSiteName(), result.getReturnType());
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

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createValidateStepClient("test Validation Error generation 2/3");

		resp = performValidateStep(client, result.getFileKey(), result.getEaId(), result.getSiteName(), result.getReturnType());
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);

		client = createCompleteStepClient("test Validation Error generation 3/3");

		resp = performCompleteStep(client, result.getFileKey());
		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(NOTIFICATION.getCode());
	}

	// COMPLETE STEP START

	// TODO may/may not implement
	//	@Test
	//	public void testInvalidCompleteInteface()
	//	{
	//	}

	/**
	 * Create's a Jersey Client object ready for POST request used in Upload step
	 * @param testName
	 * @return
	 */
	private Client createUploadStepClient(String testName)
	{
		final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
		configuration.setChunkedEncodingEnabled(false);

		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build(testName).register(MultiPartFeature.class);
		client.property(ClientProperties.READ_TIMEOUT, 10000);

		return client;
	}

	/**
	 * Make POST request for Upload step
	 * @param client
	 * @param testFileName
	 * @param mediaType
	 * @return
	 */
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

	/**
	 * Create's a Jersey Client object ready for GET request used in Validate step
	 * @param testName
	 * @return
	 */
	private Client createValidateStepClient(String testName)
	{
		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).build(testName);

		client.property(ClientProperties.READ_TIMEOUT, 10000);

		return client;
	}

	/**
	 * Make Get request for Validate step
	 * @param client
	 * @param args
	 * @return
	 */
	private Response performValidateStep(Client client, String... args)
	{
		final String uri = String.format(URI, RULE.getLocalPort(), STEP_VALIDATE);
		final String[] paramNames =
		{ "fileKey", "eaId", "siteName", "returnType" };
		WebTarget wt = client.target(uri);
		int i = 0;

		for (String arg : args)
		{
			wt = wt.queryParam(paramNames[i++], arg);
		}

		final Response resp = wt.request().get(Response.class);

		return resp;
	}

	/**
	 * Create's a Jersey Client object ready for POST request used in Complete step
	 * @param testName
	 * @return
	 */
	private Client createCompleteStepClient(String testName)
	{
		final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
		configuration.setChunkedEncodingEnabled(false);

		final Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build(testName).register(MultiPartFeature.class);

		return client;
	}

	/**
	 * Make POST request for Complete step
	 * @param client
	 * @param fileKey
	 * @return
	 */
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

	/**
	 * Extract JSON data from Response
	 * @param resp
	 * @return
	 */
	private DataExchangeResult getResultFromResponse(Response resp)
	{
		final Gson gson = new Gson();

		return gson.fromJson(resp.readEntity(String.class), DataExchangeResult.class);
	}
}
