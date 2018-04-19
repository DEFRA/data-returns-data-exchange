package uk.gov.defra.datareturns.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.fromJson;
import static uk.gov.defra.datareturns.testutils.SubmissionTestUtils.runSubmissionTest;

/**
 * Integration tests for the transfers component of a submission
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PiApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration-test")
@Slf4j
public class SubmissionTransfersIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Test
    public void testNonPositiveTonnageFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_non_positive_tonnages.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors.message", hasItems("TRANSFER_TONNAGE_NOT_GREATER_THAN_ZERO", "OVERSEAS_TONNAGE_NOT_GREATER_THAN_ZERO"));
        });
    }

    @Test
    public void testEwcActivityMissing() {
        runSubmissionTest(fromJson("/data/transfers/transfer_ewc_activity_missing.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_EWC_ACTIVITY_REQUIRED"));
        });
    }


    @Test
    public void testEwcActivityInvalid() {
        runSubmissionTest(fromJson("/data/transfers/transfer_ewc_activity_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_EWC_ACTIVITY_INVALID"));
        });
    }

    @Test
    public void testTransferRecoveryAndDisposalBothSetCodeFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_recovery_and_disposal_both_set.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_BOTH_SET"));
        });
    }

    @Test
    public void testTransferRecoveryAndDisposalNoneSetCodeFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_recovery_and_disposal_none_set.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_NONE_SET"));
        });
    }

    @Test
    public void testTransferRecoveryCodeInvalidFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_recovery_code_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_RECOVERY_CODE_INVALID"));
        });
    }

    @Test
    public void testTransferDisposalCodeInvalidFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_disposal_code_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_CODE_INVALID"));
        });
    }

    @Test
    public void testTransferWithBrtAndHazardousActivityFails() {
        runSubmissionTest(fromJson("/data/transfers/transfer_brt_with_hazardous_ewc_activity.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_BRT_NOT_ALLOWED_WITH_HAZARDOUS_EWC"));
        });
    }


    @Test
    public void testDisposalWithOverseasFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_sent_for_disposal.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_NOT_ALLOWED_FOR_DISPOSAL"));
        });
    }

    @Test
    public void testTransferOverseasWithNonHazardousActivityFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_non_hazardous_activity.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_NOT_ALLOWED_FOR_NON_HAZARDOUS_EWC_ACTIVITY"));
        });
    }

    @Test
    public void testOverseasTransfersExceedOffsiteFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_exceed_offsite.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_TONNAGE_EXCEEDS_TOTAL"));
        });
    }

    @Test
    public void testOverseasInvalidCountryCodeFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_invalid_country.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors[0].message", equalTo("INVALID_ISO3166-2_COUNTRY_CODE"));
            r.body("errors[1].message", equalTo("INVALID_ISO3166-2_COUNTRY_CODE"));
        });
    }

    @Test
    public void testOverseasDestinationNotOverseasFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_destination_not_overseas.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_DESTINATION_NOT_OVERSEAS"));
        });
    }

    @Test
    public void testOverseasAddressFieldLengthsFails() {
        runSubmissionTest(fromJson("/data/transfers/overseas_address_max_lengths.json"), (r) -> {
            // Expect 6 unique errors (one for each length constraint on address fields, plus one for the country code being invalid as well as too
            // long)
            final List<String> expected = Arrays.asList(
                    "ADDRESS_LINE1_MAX_LENGTH_EXCEEDED", "ADDRESS_LINE2_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_TOWN_OR_CITY_MAX_LENGTH_EXCEEDED", "ADDRESS_POST_CODE_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_COUNTRY_MAX_LENGTH_EXCEEDED", "INVALID_ISO3166-2_COUNTRY_CODE"
            );

            r.statusCode(HttpStatus.BAD_REQUEST.value());
            // This test assumes that we will have 2 errors for each error class.  One for the company address, one for the destination address
            r.body("errors.size()", is(2 * expected.size()));

            final List<Map<String, String>> errorList = r.extract().jsonPath().getList("errors");
            final List<String> errorMessages = errorList.stream().map(e -> e.get("message")).collect(Collectors.toList());
            final Set<String> uniqueErrorMessages = new HashSet<>(errorMessages);

            Assert.assertTrue("Expected 2 instances of each error type", errorMessages.size() == uniqueErrorMessages.size() * 2);
            Assert.assertTrue("Validation errors do not match expected constraint types", uniqueErrorMessages.containsAll(expected));
        });
    }
}
