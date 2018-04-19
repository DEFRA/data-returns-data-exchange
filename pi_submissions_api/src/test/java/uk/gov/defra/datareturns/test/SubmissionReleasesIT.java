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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
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
    public void testSubstanceNotApplicableToRegimeObligationFails() {
        runSubmissionTest(fromJson("/data/releases/release_invalid_substance_for_regime.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("RELEASE_SUBSTANCE_INVALID"));
        });
    }

    @Test
    public void testNonPositiveReleaseValuesFail() {
        runSubmissionTest(fromJson("/data/releases/release_non_positive_values.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors.message", hasItems("RELEASE_VALUE_NOT_GREATER_THAN_ZERO", "RELEASE_NOTIFIABLE_VALUE_NOT_GREATER_THAN_ZERO"));
        });
    }

    @Test
    public void testInvalidSubrouteForRouteFails() {
        runSubmissionTest(fromJson("/data/releases/release_subroute_invalid_for_route.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors[0].message", equalTo("RELEASE_SUBROUTE_INVALID_FOR_GIVEN_ROUTE"));
            r.body("errors[1].message", equalTo("RELEASE_SUBROUTE_INVALID_FOR_GIVEN_ROUTE"));
        });
    }

    @Test
    public void testMissingSubrouteForRouteFails() {
        runSubmissionTest(fromJson("/data/releases/release_subroute_missing_for_route.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("RELEASE_SUBROUTE_REQUIRED_FOR_GIVEN_ROUTE"));
        });
    }

    @Test
    public void testNotifiableExceedsTotalFails() {
        runSubmissionTest(fromJson("/data/releases/release_notifiable_exceeds_total.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors[0].message", equalTo("RELEASE_NOTIFIABLE_VALUE_EXCEEDS_TOTAL"));
            r.body("errors[1].message", equalTo("RELEASE_NOTIFIABLE_VALUE_EXCEEDS_TOTAL"));
        });
    }

    @Test
    public void testNotifiableWithNoReasonFails() {
        runSubmissionTest(fromJson("/data/releases/release_notifiable_no_reason.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors[0].message", equalTo("RELEASE_NOTIFIABLE_REASON_NOT_SPECIFIED"));
            r.body("errors[1].message", equalTo("RELEASE_NOTIFIABLE_REASON_NOT_SPECIFIED"));
        });
    }


}
