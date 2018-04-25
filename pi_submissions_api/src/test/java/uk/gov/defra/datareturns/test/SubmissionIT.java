package uk.gov.defra.datareturns.test;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.testcommons.framework.WebIntegrationTest;
import uk.gov.defra.datareturns.testcommons.restassured.RestAssuredRule;

import javax.inject.Inject;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.fromJson;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.runSubmissionTest;

/**
 * Integration tests submission-level property validation
 */
@RunWith(SpringRunner.class)
@WebIntegrationTest
@Slf4j
public class SubmissionIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testSimpleSubmission() {
        runSubmissionTest(fromJson("/data/valid/submission.json"), (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", Matchers.nullValue());
        });
    }

    @Test
    public void testSimpleBrtSubmission() {
        runSubmissionTest(fromJson("/data/valid/submission-brt.json"), (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", Matchers.nullValue());
        });
    }

    @Test
    public void testEmptySubmissionFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_empty.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors.message", hasItems("SUBMISSION_REPORTING_REFERENCE_INVALID", "SUBMISSION_DATE_INVALID"));
        });
    }


    @Test
    public void testReportingReferenceNotKnownFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_reporting_reference_not_known.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_REPORTING_REFERENCE_INVALID"));
        });
    }

    @Test
    public void testReportingReferenceNotConfiguredForPollutionInventoryFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_reporting_reference_not_pi.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_REPORTING_REFERENCE_NOT_CONFIGURED_FOR_PI"));
        });
    }

    @Test
    public void testSubmissionFutureDateFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_future_date.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_DATE_INVALID"));
        });
    }

    @Test
    public void testSubmissionCannotBeSubmittedWithInvalidNaceId() {
        runSubmissionTest(fromJson("/data/invalid/submission_nace_id_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_NACE_ID_INVALID"));
        });
    }


    @Test
    public void testSubmissionWithDuplicateReleasesFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_duplicate_releases.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_RELEASES_NOT_UNIQUE"));
        });
    }

    @Test
    public void testSubmissionWithDuplicateTransfersFails() {
        runSubmissionTest(fromJson("/data/invalid/submission_duplicate_transfers.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_TRANSFERS_NOT_UNIQUE"));
        });
    }
}
