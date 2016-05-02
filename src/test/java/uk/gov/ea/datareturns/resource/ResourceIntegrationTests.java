package uk.gov.ea.datareturns.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * Integration test class for the DataExchangeResource REST service. Uses
 * DropwizardAppRule so real HTTP requests are fired at the interface (using
 * grizzly server).
 * 
 * The tests are aimed mainly at verifying exceptions thrown from this service
 * which are split in to - System - returns a standard HTML error code.
 * Application - returns a standard HTML error code + an application specific
 * status code to help identify what went wrong.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest({"server.port=9120", "management.port=0"})
@SpringApplicationConfiguration(App.class)
@DirtiesContext
public class ResourceIntegrationTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIntegrationTests.class);

	public static final int SERVER_PORT = 9120;

	public final static MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");

	public final static String FILE_UNSUPPORTED_TYPE = "binary.exe";
	public final static String FILE_EMBEDDED_COMMAS = "embedded-commas.csv";
	public final static String FILE_EMBEDDED_XML_CHARS = "embedded-xml-chars.csv";
	public final static String FILE_NON_CSV_CONTENTS = "binary.csv";
	public final static String FILE_NON_CSV_CONTENTS_WITH_VALID_HEADERS_TYPE = "binary-with-valid-headers.csv";
	public final static String FILE_CSV_EMPTY = "empty.csv";
	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";
	public final static String FILE_CSV_MUTLIPLE_PERMITS = "multiple_permits.csv";
	public final static String FILE_PERMIT_NOT_FOUND = "permit-not-found.csv";
	public final static String FILE_PERMIT_FOUND = "permit-found.csv";
	public final static String FILE_INVALID_PERMIT_NO = "invalid-permit-no.csv";
	public final static String FILE_CSV_FAILURES = "failures.csv";
	public final static String FILE_CSV_SUCCESS = "success.csv";
	public final static String FILE_CSV_VALID_VALUE_CHARS = "valid-value-field-chars.csv";
	public final static String FILE_CSV_INVALID_VALUE_CHARS = "invalid-value-field-chars.csv";
	public final static String FILE_CSV_REQUIRED_FIELDS_ONLY = "required-fields-only.csv";
	public final static String FILE_CSV_REQUIRED_FIELDS_MISSING = "required-fields-missing.csv";
	public final static String FILE_CSV_DATE_FORMAT = "date-format-test.csv";
	public final static String FILE_CSV_UNRECOGNISED_FIELD_FOUND = "unrecognised-field-found.csv";
	public final static String FILE_CSV_INCONSISTENT_ROWS = "inconsistent-rows.csv";

	public final static String TRUE = "true";

	public final static String URI = "http://localhost:%d/data-exchange/%s";

	public final static String STEP_UPLOAD = "upload";
	public final static String STEP_COMPLETE = "complete";

	@Autowired
	private TestSettings testSettings;
//
//	@BeforeClass
//	public static void setUp() throws IOException {
//		createTestDirectory(miscSettings.getOutputLocation());
//	}
//
//	@AfterClass
//	public static void cleanup() throws IOException {
//		if (TRUE.equals(testSettings.getCleanupAfterTestRun().toLowerCase())) {
//			FileUtils.cleanDirectory(new File(miscSettings.getOutputLocation()));
//			FileUtils.cleanDirectory(new File(miscSettings.getUploadedLocation()));
//		}
//	}

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////// Start Application Exception handling tests
	/////////////////////////////////////////////////////////////////////////////////////////// ///////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUnsupportedFileType() {
		final Client client = createClient("test Unsupported File Type");
		final Response resp = performUploadStep(client, FILE_UNSUPPORTED_TYPE, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getAppStatusCode());
	}

	@Test
	public void testInvalidFileContents() {
		final Client client = createClient("test Binary File Contents");
		final Response resp = performUploadStep(client, FILE_NON_CSV_CONTENTS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		int sc = result.getAppStatusCode();
		assertThat(sc == ApplicationExceptionType.INCONSISTENT_CSV_RECORD.getAppStatusCode()
				|| sc == ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode());
	}

	@Test
	public void testBinaryFileContentWithValidHeaders() {
		final Client client = createClient("test Binary File Contents with Valid Headers");
		final Response resp = performUploadStep(client, FILE_NON_CSV_CONTENTS_WITH_VALID_HEADERS_TYPE, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getAppStatusCode());
	}

	/**
	 * Tests that the backend will load a csv file which only contains the
	 * mandatory fields.
	 */
	@Test
	public void testRequiredFieldsOnly() {
		final Client client = createClient("test Required Fields Only");
		final Response resp = performUploadStep(client, FILE_CSV_REQUIRED_FIELDS_ONLY, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}

	/**
	 * Tests that the backend will throw out CSV files which contain headings
	 * that we do not need
	 */
	@Test
	public void testUnrecognisedFieldsFound() {
		final Client client = createClient("test Unrecognised Field Found");
		final Response resp = performUploadStep(client, FILE_CSV_UNRECOGNISED_FIELD_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND.getAppStatusCode());
	}

	/**
	 * Tests that the backend will throw out CSV files which contain rows with inconsistent number of fields with respect to headers
	 */
	@Test
	public void testInconsistentRows() {
		final Client client = createClient("test Inconsistent CSV Rows");
		final Response resp = performUploadStep(client, FILE_CSV_INCONSISTENT_ROWS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.INCONSISTENT_CSV_RECORD.getAppStatusCode());
	}
	/**
	 * Tests that the backend will load a csv file with all supported date
	 * formats.
	 */
	@Test
	public void testDateFormats() {
		final Client client = createClient("test Date Formats");
		final Response resp = performUploadStep(client, FILE_CSV_DATE_FORMAT, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}

	/**
	 * Tests that the backend won't load a csv file that does not include all
	 * mandatory fields.
	 */
	@Test
	public void testRequiredFieldsMissing() {
		final Client client = createClient("test Required Fields Missing");
		final Response resp = performUploadStep(client, FILE_CSV_REQUIRED_FIELDS_MISSING, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode());
	}

	@Test
	public void testEmptyFile() {
		final Client client = createClient("test Empty File");
		final Response resp = performUploadStep(client, FILE_CSV_EMPTY, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(ApplicationExceptionType.FILE_EMPTY.getAppStatusCode());
	}

	@Test
	public void testMutiplePermits() {
		final Client client = createClient("test Multiple Permits");
		Response resp = performUploadStep(client, FILE_CSV_MUTLIPLE_PERMITS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
//		DataExchangeResult result = getResultFromResponse(resp);
//		String key = result.getUploadResult().getFileKey();
//		assertThat(key != null);
		
		// TODO: Test second stage using key retrieved from the first stage
	}

	@Test
	public void testInvalidPermitNumber() {
		final Client client = createClient("test Invalid Permit Number");
		final Response resp = performUploadStep(client, FILE_INVALID_PERMIT_NO, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode())
				.isEqualTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
	}

	@Test
	public void testPermitNumberNotFound() {
		final Client client = createClient("test Permit Number Not Found");
		final Response resp = performUploadStep(client, FILE_PERMIT_NOT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

		final DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
	}

	@Test
	public void testFileKeyMismatch() {
		Client client = createClient("test File Key mismatch");
		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

		resp = performCompleteStep(client, "anything", "anything");
		assertThat(resp.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());

		DataExchangeResult result = getResultFromResponse(resp);
		assertThat(result.getAppStatusCode()).isEqualTo(ApplicationExceptionType.SYSTEM_FAILURE.getAppStatusCode());
	}
	

	@Test
	public void testFileKeyMatch() {
//		TODO: Dependency injection of NO-OP Emailer 
//		Client client = createClient("test File Key match");
//		Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
//		DataExchangeResult result = getResultFromResponse(resp);
//		
//		resp = performCompleteStep(client, result.getUploadResult().getFileKey(), result.getUploadResult().getFileName());
//		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}


	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// End Application Exception handling tests
	/////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// Start System Exception handling tests
	/////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	// TODO system exception tests

	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// End System Exception handling tests
	/////////////////////////////////////////////////////////////////////////////////////////// ///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Start Content Validation tests
	/////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testValidationErrors() {
		final Client client = createClient("test Validation Errors");
		final Response resp = performUploadStep(client, FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
		//
		// final DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());
	}

	@Test
	public void testAcceptableValueFieldChars() {
		Client client = createClient("test Acceptable Value Field Characters");
		Response resp = performUploadStep(client, FILE_CSV_VALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

		// DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	@Test
	public void testUnacceptableValueFieldChars() {
		Client client = createClient("test Unacceptable Value Field Characters");
		Response resp = performUploadStep(client, FILE_CSV_INVALID_VALUE_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
		//
		// DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_FAILED_WITH_ERRORS.getAppStatusCode());
	}

	@Test
	public void testEmbeddedSeparators() {
		final Client client = createClient("test Embedded separator characters");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_COMMAS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

		// final DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	@Test
	public void testEmbeddedXMLChars() {
		final Client client = createClient("test Embedded XML Characters");
		final Response resp = performUploadStep(client, FILE_EMBEDDED_XML_CHARS, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

		// final DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// End Content Validation tests
	/////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// Start Miscellaneous tests
	/////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testPermitNumberFound() {
		final Client client = createClient("test Permit Number Found");
		final Response resp = performUploadStep(client, FILE_PERMIT_FOUND, MEDIA_TYPE_CSV);
		assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

		// final DataExchangeResult result = getResultFromResponse(resp);
		// assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// End Miscellaneous tests
	/////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Create's a Jersey Client object ready for POST request used in Upload
	 * step
	 * 
	 * @param testName
	 * @return
	 */
	private static Client createClient(String testName) {
		LOGGER.info("Creating client for test " + testName);
		ClientConfig clientConfig = new ClientConfig();
//		final JerseyClientConfiguration configuration = new JerseyClientConfiguration();
//		configuration.setChunkedEncodingEnabled(false);

		final Client client = new JerseyClientBuilder().withConfig(clientConfig).build().register(MultiPartFeature.class);
		client.property(ClientProperties.READ_TIMEOUT, (5 * 60 * 1000));

		return client;
	}

	/**
	 * POST request for Upload step
	 * 
	 * @param client
	 * @param testFileName
	 * @param mediaType
	 * @return
	 */
	private Response performUploadStep(Client client, String testFileName, MediaType mediaType) {
		Response response = null;
		final String testFilesLocation = testSettings.getTestFilesLocation();
		File testFile = new File(testFilesLocation, testFileName);

		try (final FormDataMultiPart form = new FormDataMultiPart();
				final InputStream data = ResourceIntegrationTests.class.getResourceAsStream(testFile.getAbsolutePath());) {
			final String uri = createURIForStep(STEP_UPLOAD);
			final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);

			form.bodyPart(fdp1);

			response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, form.getMediaType()), Response.class);

		} catch (IOException e) {
			throw new RuntimeException("Error performing upload", e);
		}
		return response;
	}

	/**
	 * POST request for Complete step
	 * 
	 * @param client
	 * @param fileKey
	 * @return
	 */
	private static Response performCompleteStep(Client client, String fileKey, String fileName) {
		Response response = null;
		try (final FormDataMultiPart form = new FormDataMultiPart()) {
			final FormDataBodyPart fdp1 = new FormDataBodyPart("fileKey", fileKey);
			form.bodyPart(fdp1);
			final FormDataBodyPart fdp2 = new FormDataBodyPart("userEmail", "abc@abc.com");
			form.bodyPart(fdp2);
			final FormDataBodyPart fdp3 = new FormDataBodyPart("orgFileName", fileName);
			form.bodyPart(fdp3);

			final String uri = createURIForStep(STEP_COMPLETE);
			response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, form.getMediaType()), Response.class);
		} catch (IOException e) {
			throw new RuntimeException("Error performing complete", e);
		}
		return response;
	}

	/**
	 * Extract JSON data from Response
	 * 
	 * @param resp
	 * @return
	 */
	private static DataExchangeResult getResultFromResponse(Response resp) {
		return resp.readEntity(DataExchangeResult.class);
	}

	/**
	 * Creates a directory for testing use
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void createTestDirectory(String dir) throws IOException {
		File directory = new File(dir);
		FileUtils.deleteDirectory(directory);
		FileUtils.forceMkdir(directory);
	}

	/**
	 * Create URI
	 * 
	 * @param step
	 * @return
	 */
	private static String createURIForStep(String step) {
		return String.format(URI, SERVER_PORT, step);
	}

}
