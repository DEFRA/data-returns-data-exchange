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

import static org.hamcrest.Matchers.containsInAnyOrder;
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
    public void testEmptyTransferFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_empty.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors.message", hasItems("TRANSFER_EWC_ACTIVITY_REQUIRED", "TRANSFER_WFD_DISPOSAL_AND_RECOVERY_NONE_SET"));
        });
    }

    @Test
    public void testEmptyOverseasFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_empty.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(5));
            r.body("errors.message", hasItems(
                    "OVERSEAS_RESPONSIBLE_COMPANY_NAME_NOT_SPECIFIED",
                    "OVERSEAS_RESPONSIBLE_COMPANY_ADDRESS_NOT_SPECIFIED",
                    "OVERSEAS_DESTINATION_ADDRESS_NOT_SPECIFIED",
                    "OVERSEAS_TONNAGE_NOT_SPECIFIED",
                    "OVERSEAS_METHOD_NOT_SPECIFIED"));
        });
    }

    @Test
    public void testNonPositiveTonnageFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_non_positive_tonnages.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors.message", hasItems("TRANSFER_TONNAGE_NOT_GREATER_THAN_ZERO", "OVERSEAS_TONNAGE_NOT_GREATER_THAN_ZERO"));
        });
    }

    @Test
    public void testEwcActivityMissing() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_ewc_activity_missing.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_EWC_ACTIVITY_REQUIRED"));
        });
    }


    @Test
    public void testEwcActivityInvalid() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_ewc_activity_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_EWC_ACTIVITY_INVALID"));
        });
    }

    @Test
    public void testTransferRecoveryAndDisposalBothSetCodeFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_recovery_and_disposal_both_set.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_BOTH_SET"));
        });
    }

    @Test
    public void testTransferRecoveryAndDisposalNoneSetCodeFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_recovery_and_disposal_none_set.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_NONE_SET"));
        });
    }

    @Test
    public void testTransferRecoveryCodeInvalidFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_recovery_code_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_RECOVERY_CODE_INVALID"));
        });
    }

    @Test
    public void testTransferDisposalCodeInvalidFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_disposal_code_invalid.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_WFD_DISPOSAL_CODE_INVALID"));
        });
    }

    @Test
    public void testTransferWithBrtAndHazardousActivityFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/transfer_brt_with_hazardous_ewc_activity.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("TRANSFER_BRT_NOT_ALLOWED_WITH_HAZARDOUS_EWC"));
        });
    }


    @Test
    public void testDisposalWithOverseasFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_sent_for_disposal.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_NOT_ALLOWED_FOR_DISPOSAL"));
        });
    }

    @Test
    public void testTransferOverseasWithNonHazardousActivityFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_non_hazardous_activity.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_NOT_ALLOWED_FOR_NON_HAZARDOUS_EWC_ACTIVITY"));
        });
    }

    @Test
    public void testOverseasTransfersExceedOffsiteFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_exceed_offsite.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_TONNAGE_EXCEEDS_TOTAL"));
        });
    }

    @Test
    public void testOverseasInvalidCountryCodeFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_invalid_country.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(2));
            r.body("errors[0].message", equalTo("ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE"));
            r.body("errors[1].message", equalTo("ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE"));
        });
    }

    @Test
    public void testOverseasDestinationNotOverseasFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_destination_not_overseas.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(1));
            r.body("errors[0].message", equalTo("OVERSEAS_DESTINATION_NOT_OVERSEAS"));
        });
    }


    @Test
    public void testEmptyOverseasAddressFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_address_empty.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(6));
            r.body("errors.message", containsInAnyOrder(
                    "ADDRESS_LINE1_NOT_SPECIFIED",
                    "ADDRESS_COUNTRY_NOT_SPECIFIED",
                    "ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE",

                    "ADDRESS_LINE1_NOT_SPECIFIED",
                    "ADDRESS_COUNTRY_NOT_SPECIFIED",
                    "ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE"
            ));
        });
    }

    @Test
    public void testOverseasAddressFieldLengthsFails() {
        runSubmissionTest(fromJson("/data/invalid/transfers/overseas_address_max_lengths.json"), (r) -> {
            r.statusCode(HttpStatus.BAD_REQUEST.value());
            r.body("errors.size()", is(12));
            r.body("errors.message", containsInAnyOrder(
                    "ADDRESS_LINE1_MAX_LENGTH_EXCEEDED", "ADDRESS_LINE2_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_TOWN_OR_CITY_MAX_LENGTH_EXCEEDED", "ADDRESS_POST_CODE_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_COUNTRY_MAX_LENGTH_EXCEEDED", "ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE",

                    "ADDRESS_LINE1_MAX_LENGTH_EXCEEDED", "ADDRESS_LINE2_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_TOWN_OR_CITY_MAX_LENGTH_EXCEEDED", "ADDRESS_POST_CODE_MAX_LENGTH_EXCEEDED",
                    "ADDRESS_COUNTRY_MAX_LENGTH_EXCEEDED", "ADDRESS_INVALID_ISO3166-2_COUNTRY_CODE"));
        });
    }
}
