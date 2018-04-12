package uk.gov.defra.datareturns.test;

import lombok.extern.slf4j.Slf4j;
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

import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.fromJson;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.runSubmissionTest;


/**
 * Integration tests for the releases component of a submission
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PiApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration-test")
@Slf4j
public class SubmissionReleasesIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testSubstanceNotApplicableToRegimeObligation() {
        runSubmissionTest(fromJson("/data/releases/submission-releases-invalid-substance.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
        });
    }
}
