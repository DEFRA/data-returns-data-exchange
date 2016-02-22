package uk.gov.ea.datareturns.resource;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.createDirectory;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.deleteDirectory;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.serializeToJSON;
import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.serializeToXML;
import static uk.gov.ea.datareturns.type.AppStatusCodeType.APP_STATUS_FAILED_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.AppStatusCodeType.APP_STATUS_SUCCESS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.FILE_KEY_MISMATCH;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.INVALID_CONTENTS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.MULTIPLE_PERMITS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.NO_RETURNS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.PERMIT_NOT_FOUND;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.UNSUPPORTED_FILE_TYPE;
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

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////// Start Application Exception handling tests ///////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUnsupportedFileType()
	{
		final Client client = createClient("test Unsupported File Type");
		final Response resp = performUploadStep(client, FILE_UNSUPPORTED_TYPE, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(UNSUPPORTED_FILE_TYPE.getAppStatusCode());
	}

	@Test
	public void testInvalidFileContents()
	{
		final Client client = createClient("test Invalid File Contents");
		final Response resp = performUploadStep(client, FILE_NON_CSV_CONTENTS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_CONTENTS.getAppStatusCode());
	}

	@Test
	public void testNoReturns()
	{
		final Client client = createClient("test No returns");
		final Response resp = performUploadStep(client, FILE_CSV_EMPTY, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(NO_RETURNS.getAppStatusCode());
	}

	@Test
	public void testMutiplePermits()
	{
		final Client client = createClient("test Multiple Permits");
		final Response resp = performUploadStep(client, FILE_CSV_MUTLIPLE_PERMITS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(MULTIPLE_PERMITS.getAppStatusCode());
	}

	@Test
	public void testPermitNumberNotFound()
	{
		final Client client = createClient("test Permit Number Not Found");
		final Response resp = performUploadStep(client, FILE_PERMIT_NOT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(PERMIT_NOT_FOUND.getAppStatusCode());
	}

	@Test
	public void testFileKeyMismatch()
	{
		Client client = createClient("test File Key mismatch");
		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());

		resp = performCompleteStep(client, "anything");
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(FILE_KEY_MISMATCH.getAppStatusCode());
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// End Application Exception handling tests //////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////// Start Application Exception handling tests ///////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// End System Exception handling tests ///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Start Content Validation tests ////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	// TODO expand to test individual validation failures

	@Test
	public void testValidationErrors()
	{
		final Client client = createClient("test Validation Errors");
		final Response resp = performUploadStep(client, FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());

		dumpResult(result);
	}

	@Test
	public void testAcceptableValueFieldChars()
	{
		Client client = createClient("test Acceptable Value Field Characters");
		Response resp = performUploadStep(client, FILE_CSV_VALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	@Test
	public void testUnacceptableValueFieldChars()
	{
		Client client = createClient("test Unacceptable Value Field Characters");
		Response resp = performUploadStep(client, FILE_CSV_INVALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());
	}

	@Test
	public void testEmbeddedSeparators()
	{
		final Client client = createClient("test Embedded separator characters");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_COMMAS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	@Test
	public void testEmbeddedXMLChars()
	{
		final Client client = createClient("test Embedded XML Characters");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_XML_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// End Content Validation tests //////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// Start Miscellaneous tests ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testPermitNumberFound()
	{
		final Client client = createClient("test Permit Number Found");
		final Response resp = performUploadStep(client, FILE_PERMIT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());

		dumpResult(result);
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// End Miscellaneous tests ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	// TODO missing integration tests
	// TODO missing column
	// TODO columns out of sequence
	// TODO extra columns
	// TODO extra data
	// TODO missing xsd file
	// TODO missing translation file
	// TODO missing xslt files
	// TODO large/average file uploads
	// TODO email failure
	// TODO email success (not sure if possible from here?)
	// TODO + many more

	// TODO CSV converter library (Csv2xml.java) needs forking, including/completing tests etc... also!

	/**
	 * Create's a Jersey Client object ready for POST request used in Upload step
	 * @param testName
	 * @return
	 */
	private Client createClient(String testName)
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
		final InputStream data = this.getClass().getResourceAsStream(makeFullPath(testFilesLocation, testFileName));
		final String uri = createURIForStep(STEP_UPLOAD);
		final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);

		form.bodyPart(fdp1);

		final Response resp = client.register(MultiPartFeature.class).target(uri).request().post(Entity.entity(form, form.getMediaType()), Response.class);

		return resp;
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
	 * Creates a directory for testing use
	 * @param dir
	 * @throws IOException 
	 */
	private static void createTestDirectory(String dir) throws IOException
	{
		deleteDirectory(dir);
		createDirectory(dir);
	}

	/**
	 * Set debug state from configuration file
	 */
	private static void setDebugState()
	{
		debugMode = TRUE.equals(RULE.getConfiguration().getMiscSettings().getDebugMode().toLowerCase()) ? true : false;
		testTimeout = RULE.getConfiguration().getTestSettings().getTestTimeout();

		LOGGER.debug("Debug Mode is '" + debugMode + "'");
		LOGGER.debug("Test Timeout is '" + Integer.parseInt(testTimeout) / 1000 + "' seconds");
	}

	/**
	 * Create URI
	 * @param step
	 * @return
	 */
	private String createURIForStep(String step)
	{
		return String.format(URI, RULE.getLocalPort(), step);
	}

	/**
	 * Debug method to output Result data in XML and JSON
	 * @param result
	 */
	private void dumpResult(DataExchangeResult result)
	{
		if (debugMode)
		{
			Map<SerializationFeature, Boolean> config = new HashMap<SerializationFeature, Boolean>();
			config.put(SerializationFeature.INDENT_OUTPUT, true);

			LOGGER.debug(serializeToXML(result, config));
			LOGGER.debug(serializeToJSON(result, config));
		}
	}
}
