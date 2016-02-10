package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullFilePath;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.createDirectory;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.deleteDirectory;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.serializeToJSON;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.serializeToXML;
import static uk.gov.ea.datareturns.type.AppStatusCode.APP_STATUS_FAILED_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.AppStatusCode.APP_STATUS_SUCCESS;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_CONTENTS;
import static uk.gov.ea.datareturns.type.ApplicationException.MULTIPLE_PERMITS;
import static uk.gov.ea.datareturns.type.ApplicationException.NO_RETURNS;
import static uk.gov.ea.datareturns.type.ApplicationException.PERMIT_NOT_FOUND;
import static uk.gov.ea.datareturns.type.ApplicationException.UNSUPPORTED_FILE_TYPE;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

/**
 * Integration test class for the DataExchangeResource REST service.
 * Uses DropwizardAppRule so real HTTP requests are fired at the interface (using grizzly server).
 * 
 * The tests are aimed mainly at verifying exceptions thrown from this service which are split in to -
 *     System - returns a standard HTML error code.
 *     Application - returns a standard HTML error code + an application specific status code to help identify what went wrong.
 * 
 * TODO not every single exception is tested - not sure if they ever will as e2e test should provide full coverage
 * TODO could refactor/reduce tests as many tests retest same code BUT too much is better so not a priority
 * TODO expand big time assertions - kept to a minimum to complete Trello card "Complete Upload Validation Code Changes. Back End - PART 1"
 * TODO deserialise and check individual validation failures
 */
public class ResourceIntegrationTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIntegrationTests.class);

	public final static MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");
	public final static MediaType MEDIA_TYPE_XML = new MediaType("application", "xml");

	public final static String FILE_UNSUPPORTED_TYPE = "binary.exe";
	public final static String FILE_EMBEDDED_COMMAS = "embedded-commas.csv";
	public final static String FILE_EMBEDDED_XML_CHARS = "embedded-xml-chars.csv";
	public final static String FILE_NON_CSV_CONTENTS = "binary.csv";
	public final static String FILE_CSV_EMPTY = "empty.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";
	public final static String FILE_CSV_MUTLIPLE_PERMITS = "multiple_permits.csv";
	public final static String FILE_PERMIT_NOT_FOUND = "permit-not-found.csv";
	public final static String FILE_PERMIT_FOUND = "permit-found.csv";
	public final static String FILE_CSV_FAILURES = "failures.csv";
	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_CSV_VALID_VALUE_CHARS = "valid-value-field-chars.csv";
	public final static String FILE_CSV_INVALID_VALUE_CHARS = "invalid-value-field-chars.csv";

	public final static String FILE_CONFIG = System.getProperty("configFile");

	public final static String TRUE = "true";

	public final static String URI = "http://localhost:%d/data-exchange/%s";

	public final static String STEP_UPLOAD = "upload";
	public final static String STEP_COMPLETE = "complete";

	private static Boolean debugMode;
	private static String testTimeout;

	@ClassRule
	public static final DropwizardAppRule<DataExchangeConfiguration> RULE = new DropwizardAppRule<DataExchangeConfiguration>(App.class, FILE_CONFIG);

	@BeforeClass
	public static void setUp() throws IOException
	{
		setDebugState();
		createTestDirectory(RULE.getConfiguration().getMiscSettings().getOutputLocation());
	}

	@AfterClass
	public static void cleanup() throws IOException
	{
		if (TRUE.equals(RULE.getConfiguration().getTestSettings().getCleanupAfterTestRun().toLowerCase()))
		{
			deleteDirectory(RULE.getConfiguration().getMiscSettings().getOutputLocation());
		}
	}

	// TODO combine and check actual errors
	@Test
	public void testValueField()
	{
		Client client = createUploadStepClient("test Value field vakid values");
		Response resp = performUploadStep(client, FILE_CSV_VALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		dumpResult(result);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());

		client = createUploadStepClient("test Value field invalid values");
		resp = performUploadStep(client, FILE_CSV_INVALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = getResultFromResponse(resp);
		dumpResult(result);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());
	}

	@Test
	public void testEmbeddedSeparatorsSuccess()
	{
		final Client client = createUploadStepClient("test Embedded Separators Success");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_COMMAS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}
	
	@Test
	public void testEmbeddedXMLCharsSuccess()
	{
		final Client client = createUploadStepClient("test Embedded XML Chars Success");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_XML_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}
		
	@Test
	public void testUnsupportedFileTypeFailure()
	{
		final Client client = createUploadStepClient("test Unsupported File Type Failure");
		final Response resp = performUploadStep(client, FILE_UNSUPPORTED_TYPE, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(UNSUPPORTED_FILE_TYPE.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testInvalidFileContentsFailure()
	{
		final Client client = createUploadStepClient("test Invalid File Contents Failure");
		final Response resp = performUploadStep(client, FILE_NON_CSV_CONTENTS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_CONTENTS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testNoReturnsFailure()
	{
		final Client client = createUploadStepClient("test Empty File Failure");
		final Response resp = performUploadStep(client, FILE_CSV_EMPTY, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(NO_RETURNS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testInsufficientDataFailure()
	{
		final Client client = createUploadStepClient("test NO Return(s) in file Failure");
		final Response resp = performUploadStep(client, FILE_CSV_INSUFFICIENT_DATA, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(NO_RETURNS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testMutiplePermitsFailure()
	{
		final Client client = createUploadStepClient("test Multiple Permits Failure");
		final Response resp = performUploadStep(client, FILE_CSV_MUTLIPLE_PERMITS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(MULTIPLE_PERMITS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testPermitNumberFoundSuccess()
	{
		final Client client = createUploadStepClient("test Permit Found Success");
		final Response resp = performUploadStep(client, FILE_PERMIT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testPermitNumberNotFoundFailure()
	{
		final Client client = createUploadStepClient("test Permit Not Found Failure");
		final Response resp = performUploadStep(client, FILE_PERMIT_NOT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(PERMIT_NOT_FOUND.getAppStatusCode());

		dumpResult(result);
	}

	// TODO this could be big!
	@Test
	public void testValidationErrorsFailure()
	{
		final Client client = createUploadStepClient("test Validation Errors Failure");
		final Response resp = performUploadStep(client, FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());

		dumpResult(result);
	}

	
	// TODO only as far as "upload" for now
	
	// TODO commented out temporarily to get jenkins dev working
//	@Test
//	public void testEndToEndSuccess()
//	{
//		Client client = createUploadStepClient("test End To End Success");
//		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		DataExchangeResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
//
//		dumpResult(result);
//		
//		resp = performCompleteStep(client, result.getUploadResult().getFileKey());
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
//
//		dumpResult(result);
//	}

	// TODO missing column
	// TODO columns out of sequence
	// TODO extra columns
	// TODO extra data
	// TODO missing xsd file
	// TODO missing translation file
	// TODO missing xslt files
	// TODO large/average file uploads
	// TODO + complete step tests etc...
	// TODO & loads more

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
		client.property(ClientProperties.READ_TIMEOUT, testTimeout);

		return client;
	}

	/**
	 * POST request for Upload step
	 * @param client
	 * @param testFileName
	 * @param mediaType
	 * @return
	 */
	private Response performUploadStep(Client client, String testFileName, MediaType mediaType)
	{
		final String testFilesLocation = RULE.getConfiguration().getTestSettings().getTestFilesLocation();
		final FormDataMultiPart form = new FormDataMultiPart();
		final InputStream data = this.getClass().getResourceAsStream(makeFullFilePath(testFilesLocation, testFileName));
		final String uri = createURIForStep(STEP_UPLOAD);
		final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);

		form.bodyPart(fdp1);

		final Response resp = client.register(MultiPartFeature.class).target(uri).request().post(Entity.entity(form, form.getMediaType()), Response.class);

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
	 * POST request for Complete step
	 * @param client
	 * @param fileKey
	 * @return
	 */
	private Response performCompleteStep(Client client, String fileKey)
	{
		final FormDataMultiPart form = new FormDataMultiPart();
		final FormDataBodyPart fdp1 = new FormDataBodyPart("fileKey", fileKey);
		form.bodyPart(fdp1);
		final FormDataBodyPart fdp2 = new FormDataBodyPart("userEmail", "abc@abc.com");
		form.bodyPart(fdp2);

		final String uri = createURIForStep(STEP_COMPLETE);
		final Response resp = client.register(MultiPartFeature.class).target(uri).request().post(Entity.entity(form, form.getMediaType()), Response.class);

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

	/**
	 * Creates a "new" directory for testing use
	 * @param dir
	 * @throws IOException 
	 */
	private static void createTestDirectory(String dir) throws IOException
	{
		deleteDirectory(dir);
		createDirectory(dir);
	}

	private static void setDebugState()
	{
		debugMode = TRUE.equals(RULE.getConfiguration().getMiscSettings().getDebugMode().toLowerCase()) ? true : false;
		testTimeout = RULE.getConfiguration().getTestSettings().getTestTimeout();

		LOGGER.debug("Debug Mode is '" + debugMode + "'");
		LOGGER.debug("Test Timeout is '" + Integer.parseInt(testTimeout) / 1000 + "' seconds");
	}

	private String createURIForStep(String step)
	{
		return String.format(URI, RULE.getLocalPort(), step);
	}

	// TODO DEBUG
	private void dumpResult(DataExchangeResult result)
	{
		if (debugMode)
		{
			Map<SerializationFeature, Boolean> config = new HashMap<SerializationFeature, Boolean>();
			config.put(SerializationFeature.INDENT_OUTPUT, true);

			System.out.println(serializeToXML(result, config));
			System.out.println(serializeToJSON(result, config));
		}
	}
}
