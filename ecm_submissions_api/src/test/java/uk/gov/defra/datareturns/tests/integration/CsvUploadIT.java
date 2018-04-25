package uk.gov.defra.datareturns.tests.integration;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.exceptions.ApplicationExceptionType;
import uk.gov.defra.datareturns.testcommons.framework.WebIntegrationTest;
import uk.gov.defra.datareturns.testcommons.restassured.RestAssuredRule;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.defra.datareturns.testutils.EcmTestUtils.runMultipartUploadTest;

/**
 * CSV Upload integration tests.
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@WebIntegrationTest
public class CsvUploadIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testSimpleUpload() {
        runMultipartUploadTest("/data/csv-uploads/success.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testBinaryExe() {
        runMultipartUploadTest("/data/csv-uploads/binary.exe", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED.getReason()));
            r.body("errors[0].line_number", nullValue());
        });
    }

    @Test
    public void testBinaryCsv() {
        runMultipartUploadTest("/data/csv-uploads/binary.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getReason()));
            r.body("errors[0].line_number", equalTo(1));
        });
    }

    @Test
    public void testBinaryCsvWithHeaders() {
        runMultipartUploadTest("/data/csv-uploads/binary-with-valid-headers.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getReason()));
            r.body("errors[0].line_number", equalTo(2));
        });
    }


    @Test
    public void testEmptyFile() {
        runMultipartUploadTest("/data/csv-uploads/empty.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.FILE_EMPTY.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.FILE_EMPTY.getReason()));
        });
    }

    /**
     * Tests that the backend will load a csv file which only contains the
     * mandatory fields.
     */
    @Test
    public void testRequiredFieldsOnly() {
        runMultipartUploadTest("/data/csv-uploads/required-fields-only.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", Matchers.nullValue());
        });
    }

    /**
     * Tests that the backend won't load a csv file that does not include all mandatory fields.
     */
    @Test
    public void testRequiredFieldsMissing() {
        runMultipartUploadTest("/data/csv-uploads/required-fields-missing.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.HEADER_MANDATORY_FIELD_MISSING.getReason()));
            r.body("errors[0].line_number", equalTo(1));
        });
    }

    /**
     * Tests that the backend will throw out CSV files which contain headings
     * that we do not need
     */
    @Test
    public void testUnrecognisedFieldFound() {
        runMultipartUploadTest("/data/csv-uploads/unrecognised-field-found.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.HEADER_UNRECOGNISED_FIELD_FOUND.getReason()));
            r.body("errors[0].line_number", equalTo(1));
        });
    }

    /**
     * Tests that the backend will throw out CSV files which contain rows with inconsistent number of fields with respect to headers
     */
    @Test
    public void testInconsistentRows() {
        runMultipartUploadTest("/data/csv-uploads/inconsistent-rows.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getAppStatusCode()));
            r.body("errors[0].message", equalTo(ApplicationExceptionType.FILE_STRUCTURE_EXCEPTION.getReason()));
            r.body("errors[0].line_number", equalTo(4));
        });
    }

    /**
     * Tests that the backend will load a csv file with all supported date formats.
     */
    @Test
    public void testDateFormats() {
        runMultipartUploadTest("/data/csv-uploads/date-format-test.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    /**
     * Tests that the backend will load a csv file with all supported date formats.
     */
    @Test
    public void testDateFormatsInvalid() {
        runMultipartUploadTest("/data/csv-uploads/date-format-invalid-test.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9020-Missing"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Mon_Date", equalTo(""));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));


            r.body("errors[1].error_class", equalTo("DR9020-Incorrect"));
            r.body("errors[1].instances.size()", is(4));
            r.body("errors[1].instances[0].invalid_values.Mon_Date", equalTo("16-04-15"));
            r.body("errors[1].instances[0].line_numbers", hasItems(3));
            r.body("errors[1].instances[1].invalid_values.Mon_Date", equalTo("15/04/16"));
            r.body("errors[1].instances[1].line_numbers", hasItems(4));
            r.body("errors[1].instances[2].invalid_values.Mon_Date", equalTo("32/01/2016"));
            r.body("errors[1].instances[2].line_numbers", hasItems(5));
            r.body("errors[1].instances[3].invalid_values.Mon_Date", equalTo("17-04-2016T9:4:59"));
            r.body("errors[1].instances[3].line_numbers", hasItems(6));
        });
    }


    @Test
    public void testMutiplePermits() {
        final String resource = "/data/csv-uploads/multiple_permits.csv";
        runMultipartUploadTest(resource, (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("filename", equalTo(resource));
            r.body("errors", nullValue());

            // Check we have 2 datasets associated with the upload.
            final String datasetsCollectionHref = r.extract().jsonPath().getString("_links.datasets.href");
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(datasetsCollectionHref)
                    .then()
                    .body("_embedded.datasets.size()", is(2));
        });
    }

    @Test
    public void testInvalidPermitNumber() {
        runMultipartUploadTest("/data/csv-uploads/invalid-permit-no.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));

            r.body("errors[0].error_class", equalTo("DR9000-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.EA_ID", equalTo("An invalid permit number"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));

            r.body("errors[1].error_class", equalTo("DR9000-Missing"));
            r.body("errors[1].instances.size()", is(1));
            r.body("errors[1].instances[0].invalid_values.EA_ID", equalTo(""));
            r.body("errors[1].instances[0].line_numbers", hasItems(3));
        });
    }

    @Test
    public void testPermitSiteMismatch() {
        runMultipartUploadTest("/data/csv-uploads/permit-site-mismatch.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9110-Conflict"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.EA_ID", equalTo("AP3533LF"));
            r.body("errors[0].instances[0].invalid_values.Site_Name", equalTo("Not the right site name for AP3533LF"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }


    @Test
    public void testAcceptableValueFieldChars() {
        runMultipartUploadTest("/data/csv-uploads/valid-value-field-chars.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testUnacceptableValueFieldChars() {
        runMultipartUploadTest("/data/csv-uploads/invalid-value-field-chars.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));

            r.body("errors[0].error_class", equalTo("DR9999-Missing"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Value", equalTo(""));
            r.body("errors[0].instances[0].invalid_values.Txt_Value", equalTo(""));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));

            r.body("errors[1].error_class", equalTo("DR9040-Incorrect"));
            r.body("errors[1].instances.size()", is(3));
            r.body("errors[1].instances[0].invalid_values.Value", equalTo("<"));
            r.body("errors[1].instances[0].line_numbers", hasItems(3));
            r.body("errors[1].instances[1].invalid_values.Value", equalTo(">"));
            r.body("errors[1].instances[1].line_numbers", hasItems(4));
            r.body("errors[1].instances[2].invalid_values.Value", equalTo("<-.1"));
            r.body("errors[1].instances[2].line_numbers", hasItems(5));
        });
    }

    @Test
    public void testEmbeddedSeparators() {
        runMultipartUploadTest("/data/csv-uploads/embedded-commas.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testMethStandValid() {
        runMultipartUploadTest("/data/csv-uploads/testMethStand.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testMethStandInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testMethStandInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9100-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Meth_Stand", equalTo("Invalid Meth_Stand"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }

    @Test
    public void testReturnTypeValid() {
        runMultipartUploadTest("/data/csv-uploads/testReturnType.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testReturnTypeInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testReturnTypeInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9010-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Rtn_Type", equalTo("Invalid Return Type"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }

    @Test
    public void testRefPeriodValid() {
        runMultipartUploadTest("/data/csv-uploads/testRefPeriod.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testRefPeriodInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testRefPeriodInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9090-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Ref_Period", equalTo("Invalid Reference Period"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }

    @Test
    public void testUnitsValid() {
        runMultipartUploadTest("/data/csv-uploads/testUnits.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testUnitsInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testUnitsInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9050-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Unit", equalTo("Invalid Unit"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }

    @Test
    public void testQualifiersValid() {
        runMultipartUploadTest("/data/csv-uploads/testQualifier.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testQualifiersInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testQualifierInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9180-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Qualifier", equalTo("Invalid Qualifier"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }

    @Test
    public void testReturnPeriodValid() {
        runMultipartUploadTest("/data/csv-uploads/testReturnPeriod.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testReturnPeriodInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testReturnPeriodInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9070-Incorrect"));
            r.body("errors[0].instances.size()", is(6));
            r.body("errors[0].instances[0].invalid_values.Rtn_Period", equalTo("Week 99"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
            r.body("errors[0].instances[1].invalid_values.Rtn_Period", equalTo("Spurs 2017"));
            r.body("errors[0].instances[1].line_numbers", hasItems(3));
            r.body("errors[0].instances[2].invalid_values.Rtn_Period", equalTo("Qtr 5 2017"));
            r.body("errors[0].instances[2].line_numbers", hasItems(4));
            r.body("errors[0].instances[3].invalid_values.Rtn_Period", equalTo("2017a"));
            r.body("errors[0].instances[3].line_numbers", hasItems(5));
            r.body("errors[0].instances[4].invalid_values.Rtn_Period", equalTo("2016-2017"));
            r.body("errors[0].instances[4].line_numbers", hasItems(6));
            r.body("errors[0].instances[5].invalid_values.Rtn_Period", equalTo("Tottenham Hotsyear 1963"));
            r.body("errors[0].instances[5].line_numbers", hasItems(7));
        });
    }

    @Test
    public void testParameterValid() {
        runMultipartUploadTest("/data/csv-uploads/testParameter.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testParameterInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testParameterInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));

            r.body("errors[0].error_class", equalTo("DR9030-Missing"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Parameter", equalTo(""));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));


            r.body("errors[1].error_class", equalTo("DR9030-Incorrect"));
            r.body("errors[1].instances.size()", is(1));
            r.body("errors[1].instances[0].invalid_values.Parameter", equalTo("Unknown Parameter"));
            r.body("errors[1].instances[0].line_numbers", hasItems(3));
        });
    }

    @Test
    public void testTextValueValid() {
        runMultipartUploadTest("/data/csv-uploads/testTextValue.csv", (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", nullValue());
        });
    }

    @Test
    public void testTextValueInvalid() {
        runMultipartUploadTest("/data/csv-uploads/testTextValueInvalid.csv", (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("error_code", equalTo(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode()));
            r.body("errors[0].error_class", equalTo("DR9080-Incorrect"));
            r.body("errors[0].instances.size()", is(1));
            r.body("errors[0].instances[0].invalid_values.Txt_Value", equalTo("Invalid Text Value"));
            r.body("errors[0].instances[0].line_numbers", hasItems(2));
        });
    }
}
