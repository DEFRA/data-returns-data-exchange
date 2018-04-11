package uk.gov.defra.datareturns.test;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.PiApi;
import uk.gov.defra.datareturns.test.rules.RestAssuredRule;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PiApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration-test")
@Slf4j
public class SubmissionIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testSimpleSubmission() throws IOException {
        final String testJson = IOUtils.toString(SubmissionIT.class.getResourceAsStream("/data/submission.json"), StandardCharsets.UTF_8);

        runSubmissionTest(testJson, (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
        });
    }

    @Test
    public void testSubstanceNotApplicableToRegimeObligation() throws IOException {
        final String testJson = IOUtils.toString(SubmissionIT.class.getResourceAsStream("/data/submission-releases-invalid-substance.json"),
                StandardCharsets.UTF_8);

        runSubmissionTest(testJson, (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
        });
    }


    @Test
    public void testOverseasTransfersExceedOffsiteFails() throws IOException {
        final String testJson = IOUtils.toString(SubmissionIT.class.getResourceAsStream("/data/transfers/overseas_exceed_offsite.json"),
                StandardCharsets.UTF_8);

        runSubmissionTest(testJson, (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors[0].message", Matchers.equalTo("OVERSEAS_TONNAGE_EXCEEDS_TOTAL"));
        });
    }


    @Test
    public void testOverseasTransfersWithBrtOffsiteFails() throws IOException {
        final String testJson = IOUtils.toString(SubmissionIT.class.getResourceAsStream("/data/transfers/overseas_with_brt_transfer.json"),
                StandardCharsets.UTF_8);

        runSubmissionTest(testJson, (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors[0].message", Matchers.equalTo("OVERSEAS_NOT_ALLOWED_WITH_BRT_TRANSFER"));
        });
    }


    private void runSubmissionTest(final String submissionJson, final Consumer<ValidatableResponse> responseAssertions) {
        ValidatableResponse response = null;

        try {
            response =
                    given()
                            .contentType(ContentType.JSON)
                            .body(submissionJson)
                            .when()
                            .post("/submissions")
                            .then()
                            .log().all();

            responseAssertions.accept(response);
        } finally {
            // If the call above created a submission, then it is deleted again here.
            if (response != null) {
                final String locationHeader = response.extract().header("Location");
                if (locationHeader != null) {
                    given()
                            .contentType(ContentType.JSON)
                            .when()
                            .delete(locationHeader)
                            .then()
                            .log().all()
                            .statusCode(HttpStatus.NO_CONTENT.value());
                }

            }
        }
    }
}
