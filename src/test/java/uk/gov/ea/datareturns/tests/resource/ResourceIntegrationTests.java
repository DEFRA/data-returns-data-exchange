package uk.gov.ea.datareturns.tests.resource;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.web.security.ApiKeys;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

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

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@RunWith(SpringRunner.class)
@ActiveProfiles("IntegrationTests")
public class ResourceIntegrationTests {

    @Inject
    ApiKeys apiKeys;

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

    public final static String FILE_PERMIT_SITE_MISMATCH = "permit-site-mismatch.csv";

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

    public final static String URI = "http://localhost:%d/%s";

    public final static String STEP_UPLOAD = "data-exchange/upload";

    public final static String STEP_COMPLETE = "data-exchange/complete";

    public final static String CONTROLLED_LISTS = "controlled-list/lists";
    public final static String CONTROLLED_LISTS_NAVIGATION = "controlled-list/nav";
    public final static String TEST_SEARCH = "lookup/permit?term=Dogsthorpe";

    public final static String METH_STAND_VALID = "validation/testMethStand.csv";
    public final static String METH_STAND_INVALID = "validation/testMethStandInvalid.csv";

    public final static String RTN_TYPE_VALID = "validation/testReturnType.csv";
    public final static String RTN_TYPE_INVALID = "validation/testReturnTypeInvalid.csv";

    public final static String REF_PERIOD_VALID = "validation/testRefPeriod.csv";
    public final static String REF_PERIOD_INVALID = "validation/testRefPeriodInvalid.csv";

    public final static String UNITS_VALID = "validation/testUnits.csv";
    public final static String UNITS_INVALID = "validation/testUnitsInvalid.csv";

    public final static String QUALIFIERS_VALID = "validation/testQualifier.csv";
    public final static String QUALIFIERS_INVALID = "validation/testQualifierInvalid.csv";

    public final static String RETURN_PERIOD_VALID = "validation/testReturnPeriod.csv";
    public final static String RETURN_PERIOD_INVALID = "validation/testReturnPeriodInvalid.csv";

    public final static String PARAMETERS_VALID = "validation/testParameter.csv";
    public final static String PARAMETERS_INVALID = "validation/testParameterInvalid.csv";

    public final static String TEXT_VALUE_VALID = "validation/testTextValue.csv";
    public final static String TEXT_VALUE_INVALID = "validation/testTextValueInvalid.csv";

    public final static String REM_VALID_PARAMETER = "validation/testDependencyREMValid.csv";
    public final static String REM_INVALID_PARAMETER = "validation/testDependencyREMInvalid.csv";
    public final static String REM_INVALID_UNIT = "validation/testDependencyREMInvalidUnit.csv";

    public final static String PI_VALID = "validation/testDependencyPIValid.csv";
    public final static String PI_INVALID_NO_TRANSFER = "validation/testDependencyPINoTransfers.csv";

    @Inject
    private TestSettings testSettings;

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
        final int sc = result.getAppStatusCode();
        assertThat(sc).isIn(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode(),
                ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode());
    }

    @Test
    public void testBinaryFileContentWithValidHeaders() {
        final Client client = createClient("test Binary File Contents with Valid Headers");
        final Response resp = performUploadStep(client, FILE_NON_CSV_CONTENTS_WITH_VALID_HEADERS_TYPE, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        final int sc = result.getAppStatusCode();
        assertThat(sc).isIn(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode(),
                ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getAppStatusCode());
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
        final Client client = createClient("test Unrecognised MappedField Found");
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
                .isEqualTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode());
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

        final DataExchangeResult result = getResultFromResponse(resp);
        final String key = result.getUploadResult().getFileKey();
        assertThat(key).isNotNull();
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
        final Response resp = performUploadStep(client, FILE_CSV_MUTLIPLE_PERMITS, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
        //		DataExchangeResult result = getResultFromResponse(resp);
        //		String key = result.getUploadResult().getFileKey();
        //		assertThat(key).isNotNull();

        // TODO: Test second stage using key retrieved from the first stage
    }

    @Test
    public void testInvalidPermitNumber() {
        final Client client = createClient("test Invalid UniqueIdentifier Number");
        final Response resp = performUploadStep(client, FILE_INVALID_PERMIT_NO, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        assertThat(result.getAppStatusCode())
                .isEqualTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
    }
    @Test
    public void testPermitSiteMismatch() {
        final Client client = createClient("test EA_ID and Site_Name mismatch.");
        final Response resp = performUploadStep(client, FILE_PERMIT_SITE_MISMATCH, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        assertThat(result.getAppStatusCode())
                .isEqualTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
    }

    @Test
    public void testPermitNumberNotFound() {
        final Client client = createClient("test UniqueIdentifier Number Not Found");
        final Response resp = performUploadStep(client, FILE_PERMIT_NOT_FOUND, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        assertThat(result.getAppStatusCode()).isEqualTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode());
    }

    @Test
    public void testFileKeyMismatch() {
        final Client client = createClient("test File Key mismatch");
        Response resp = performUploadStep(client, FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

        resp = performCompleteStep(client, "anything", "anything");
        assertThat(resp.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
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

    /*
     * Test basic controlled lists functionality
     */
    @Test
    public void testControlledListBadList() {
        Client client = createClient("test Controlled List Bad List");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/not-a-list";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testListMetadata() {
        Client client = createClient("test List Metadata");
        final String uri = createURIForStep(CONTROLLED_LISTS);
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListParameters() {
        Client client = createClient("test Controlled List Parameters");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/parameters";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListUnits() {
        Client client = createClient("test Controlled List Units");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/units";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListRefPeriod() {
        Client client = createClient("test Controlled List Reference Period");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/ref_period";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListMethodOrStandard() {
        Client client = createClient("test Controlled List Method Or Standard");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/method";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListReturnType() {
        Client client = createClient("test Controlled List Return Type");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/rtn_type";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    /*
     * Run all the test again to test the caching
     */
    @Test
    public void testControlledListParameters2() {
        Client client = createClient("test Controlled List Parameters2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/parameters";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListUnits2() {
        Client client = createClient("test Controlled List Units2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/units";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListRefPeriod2() {
        Client client = createClient("test Controlled List Reference Period2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/ref_period";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListMethodOrStandard2() {
        Client client = createClient("test Controlled List Method Or Standard2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/method";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListReturnType2() {
        Client client = createClient("test Controlled List Return Type2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/rtn_type";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    /*
     * Test extended controlled lists functionality - navigation/validation
     * These are a small set of tests....not going to duplicate all the integration tests here
     */
    @Test
    public void testListReturnTypes() {
        Client client = createClient("Nav: List Return Types");
        final String uri = createURIForStep(CONTROLLED_LISTS_NAVIGATION);
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testREMValidParameter() {
        Client client = createClient("Nav: REM valid parameter");
        final Response response = performUploadStep(client, REM_VALID_PARAMETER, MEDIA_TYPE_CSV);
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testREMInvalidParameter() {
        Client client = createClient("Nav: REM valid parameter");
        final Response response = performUploadStep(client, REM_INVALID_PARAMETER, MEDIA_TYPE_CSV);
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testREMInvalidUnit() {
        Client client = createClient("Nav: REM invalid unit");
        final Response response = performUploadStep(client, REM_INVALID_UNIT, MEDIA_TYPE_CSV);
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testPIValid() {
        Client client = createClient("Nav: PI valid");
        final Response response = performUploadStep(client, PI_VALID, MEDIA_TYPE_CSV);
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testPIInvalidNoTransfer() {
        Client client = createClient("Nav: PI invalid");
        final Response response = performUploadStep(client, PI_INVALID_NO_TRANSFER, MEDIA_TYPE_CSV);
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
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
        final Client client = createClient("test Acceptable Value MappedField Characters");
        final Response resp = performUploadStep(client, FILE_CSV_VALID_VALUE_CHARS, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());

        // DataExchangeResult result = getResultFromResponse(resp);
        // assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS.getAppStatusCode());
    }

    @Test
    public void testUnacceptableValueFieldChars() {
        final Client client = createClient("test Unacceptable Value MappedField Characters");
        final Response resp = performUploadStep(client, FILE_CSV_INVALID_VALUE_CHARS, MEDIA_TYPE_CSV);
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

    @Test
    public void testMethStandValid() {
        final Client client = createClient("test Meth_Stand valid");
        final Response resp = performUploadStep(client, METH_STAND_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testMethStandInvalid() {
        final Client client = createClient("test Meth_Stand invalid");
        final Response resp = performUploadStep(client, METH_STAND_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9100
        )).isTrue();

    }

    @Test
    public void testReturnTypeValid() {
        final Client client = createClient("test Rtn_Type valid");
        final Response resp = performUploadStep(client, RTN_TYPE_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testReturnTypeInvalid() {
        final Client client = createClient("test Rtn_Type invalid");
        final Response resp = performUploadStep(client, RTN_TYPE_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9010
        )).isTrue();
    }

    @Test
    public void testRefPeriodValid() {
        final Client client = createClient("test Ref_Period valid");
        final Response resp = performUploadStep(client, REF_PERIOD_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testRefPeriodInvalid() {
        final Client client = createClient("test Ref_Period invalid");
        final Response resp = performUploadStep(client, REF_PERIOD_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9090
        )).isTrue();
    }

    @Test
    public void testUnitsValid() {
        final Client client = createClient("test Units valid");
        final Response resp = performUploadStep(client, UNITS_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testUnitsInvalid() {
        final Client client = createClient("test Units invalid");
        final Response resp = performUploadStep(client, UNITS_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9050
        )).isTrue();
    }

    @Test
    public void testQualifiersValid() {
        final Client client = createClient("test Qualifiers valid");
        final Response resp = performUploadStep(client, QUALIFIERS_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testQualifiersInvalid() {
        final Client client = createClient("test Qualifiers invalid");
        final Response resp = performUploadStep(client, QUALIFIERS_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9180
        )).isTrue();
    }

    @Test
    public void testReturnPeriodValid() {
        final Client client = createClient("test Rtn_Period valid");
        final Response resp = performUploadStep(client, RETURN_PERIOD_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testReturnPeriodInvalid() {
        final Client client = createClient("test Rtn_Period invalid");
        final Response resp = performUploadStep(client, RETURN_PERIOD_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();

        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 3L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();

        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 4L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();

        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 5L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();

        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 6L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();

        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 7L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9070
        )).isTrue();
    }

    @Test
    public void testParameterValid() {
        final Client client = createClient("test Parameter valid");
        final Response resp = performUploadStep(client, PARAMETERS_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testParameterInvalid() {
        final Client client = createClient("test Parameter invalid");
        final Response resp = performUploadStep(client, PARAMETERS_INVALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();

        // Test missing
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 2L &&
                        e.getErrorType().equals("Missing") &&
                        e.getErrorCode() == 9030
        )).isTrue();

        // Test incorrect
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 3L &&
                        e.getErrorType().equals("Incorrect") &&
                        e.getErrorCode() == 9030
        )).isTrue();
    }

    @Test
    public void testTextValueValid() {
        final Client client = createClient("test Text Value valid");
        final Response resp = performUploadStep(client, TEXT_VALUE_VALID, MEDIA_TYPE_CSV);
        assertThat(resp.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testTextValueInvalid() {
        final Client client = createClient("test Text Value invalid");
        final Response resp = performUploadStep(client, TEXT_VALUE_INVALID, MEDIA_TYPE_CSV);

        assertThat(resp.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final DataExchangeResult result = getResultFromResponse(resp);
        ValidationErrors validationErrors = result.getValidationErrors();

        // Only one of value and text value
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 4L &&
                        e.getErrorType().equals("Conflict") &&
                        e.getErrorCode() == 9999
        )).isTrue();

        // Units must not be used with text value
        assertThat(validationErrors.getErrors().stream().anyMatch(e ->
                e.getLineNumber() == 4L &&
                        e.getErrorType().equals("Conflict") &&
                        e.getErrorCode() == 9050
        )).isTrue();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// End Content Validation tests
    /////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// Tests for the permit lookup tool
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testSearch() {
        final Client client = createClient("test Search");
        final String uri = createURIForStep(TEST_SEARCH);
        final Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// Start Miscellaneous tests
    /////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPermitNumberFound() {
        final Client client = createClient("test UniqueIdentifier Number Found");
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
    private static Client createClient(final String testName) {
        LOGGER.info("Creating client for test " + testName);
        final ClientConfig clientConfig = new ClientConfig();
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
    private Response performUploadStep(final Client client, final String testFileName, final MediaType mediaType) {
        Response response = null;
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);

        try (
                final FormDataMultiPart form = new FormDataMultiPart();
                final InputStream data = ResourceIntegrationTests.class.getResourceAsStream(testFile.getAbsolutePath())) {
            final String uri = createURIForStep(STEP_UPLOAD);
            final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);

            form.bodyPart(fdp1);

            response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, this.apiKeys.calculateAuthorizationHeader(testFileName))
                    .header("filename", testFileName)
                    .post(Entity.entity(form, form.getMediaType()), Response.class);

        } catch (final IOException e) {
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
    private Response performCompleteStep(final Client client, final String fileKey, final String fileName) {
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
                    .header(HttpHeaders.AUTHORIZATION, this.apiKeys.calculateAuthorizationHeader(fileName))
                    .header("filename", fileName)
                    .post(Entity.entity(form, form.getMediaType()), Response.class);
        } catch (final IOException e) {
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
    private static DataExchangeResult getResultFromResponse(final Response resp) {
        return resp.readEntity(DataExchangeResult.class);
    }

    /**
     * Create URI
     *
     * @param step
     * @return
     */
    private static String createURIForStep(final String step) {
        return String.format(URI, SERVER_PORT, step);
    }
}
