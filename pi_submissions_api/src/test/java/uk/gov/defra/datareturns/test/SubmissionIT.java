package uk.gov.defra.datareturns.test;

import lombok.extern.slf4j.Slf4j;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.fromJson;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.runSubmissionTest;

/**
 * Integration tests submission-level property validation
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PiApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration-test")
@Slf4j
public class SubmissionIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testSimpleSubmission() {
        runSubmissionTest(fromJson("/data/submission.json"), (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", Matchers.nullValue());
        });
    }

    @Test
    public void testSimpleBrtSubmission() {
        runSubmissionTest(fromJson("/data/submission-brt.json"), (r) -> {
            r.statusCode(HttpStatus.CREATED.value());
            r.body("errors", Matchers.nullValue());
        });
    }

    @Test
    public void testSubmissionFutureDateFails() {
        runSubmissionTest(fromJson("/data/submission_future_date.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_DATE_INVALID"));
        });
    }

    @Test
    public void testSubmissionWithDuplicateReleasesFails() {
        runSubmissionTest(fromJson("/data/submission_duplicate_releases.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_RELEASES_NOT_UNIQUE"));
        });
    }

    @Test
    public void testSubmissionWithDuplicateTransfersFails() {
        runSubmissionTest(fromJson("/data/submission_duplicate_transfers.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("SUBMISSION_TRANSFERS_NOT_UNIQUE"));
        });
    }
}
