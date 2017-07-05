package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.tests.integration.api.v1.AbstractDataResourceTests;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetStatus;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetValidity;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 *
 * Replace the .csv tests - both the processor and resource with equivelent tests
 * which use the API
 */
@RunWith(Parameterized.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class APIResourceIntegrationTests extends AbstractDataResourceTests {

    private final String name;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule  springMethodRule = new SpringMethodRule();

    @FunctionalInterface
    private interface ResourceIntegrationTestExpectations {
        boolean passes(ResourceIntegrationTestResult t);
    }

    public static final String HTTP_1_1_201_CREATED = "HTTP/1.1 201 Created";

    // An ordered map of all the single record resource tests to be run in turn
    private static final Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> SINGLE_RECORD_RESOURCE_TESTS;

    // An ordered map of all the multi record resource tests to be run in turn
    private static final Map<String, Pair<DataSamplePayload[], ResourceIntegrationTestExpectations>> MULTI_RECORD_RESOURCE_TESTS;

    // The following suppliers create the payloads used in the tests
    private static final Supplier<DataSamplePayload> EMPTY_PAYLOAD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload();
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> REQUIRED_FIELDS = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload();
        dataSamplePayload.setEaId("42355");
        dataSamplePayload.setSiteName("Biffa - Marchington Landfill Site");
        dataSamplePayload.setReturnType("Air point source emissions");
        dataSamplePayload.setMonitoringPoint("Borehole 1");
        dataSamplePayload.setMonitoringDate("2015-02-15");
        dataSamplePayload.setParameter("1,3-Dichloropropene");
        dataSamplePayload.setValue("<103.2");
        dataSamplePayload.setUnit("ug");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload[]> SUPPORTED_DATE_FORMATS = () -> {
        String[] testDateFormats = new String[] {
                "2016-04-15", "2016-04-16T09:04:59", "2016-04-16 09:04:59", "17-04-2016",
                "17-04-2016T09:04:59", "17-04-2016 09:04:59", "17/04/2016", "17/04/2016T09:04:59",
                "17/04/2016 09:04:59"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[testDateFormats.length];

        for (int i = 0; i < testDateFormats.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setMonitoringDate(testDateFormats[i]);
        }

        return dataSamplePayloads;
    };

    private static final Supplier<DataSamplePayload> PERMIT_NOT_FOUND = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setEaId("ZZZZ");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> PERMIT_SITE_MISMATCH = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setSiteName("Biffa - Saredon Hill Quarry");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_PERMIT = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setEaId(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_RETURN_TYPE = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setReturnType("Not a return type");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> MISSING_RETURN_TYPE = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setReturnType(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_PARAMETER = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setParameter("Not a parameter");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> MISSING_PARAMETER = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setParameter(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_UNIT = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setUnit("Not a unit");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> MISSING_UNIT = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setUnit(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_RETURN_PERIOD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setReturnPeriod("Not a return period");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> MISSING_MONITORING_POINT = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setMonitoringPoint(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> LONG_MONITORING_POINT = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setMonitoringPoint(StringUtils.repeat("A", 51));
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload[]> VALID_QUALIFIERS = () -> {
        String[] validValueFields = new String[] {
                "All isomers", "At 20°c", "In leachate"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validValueFields.length];

        for (int i = 0; i < validValueFields.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setQualifier(validValueFields[i]);
        }

        return dataSamplePayloads;
    };

    private static final Supplier<DataSamplePayload> INVALID_QUALIFIER = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setQualifier("Not a qualifier");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload[]> VALID_METHOD_OR_STANDARD = () -> {
        String[] methodOrStandard = new String[] {
                "MMS11", "MMS12", "MMS13"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[methodOrStandard.length];

        for (int i = 0; i < methodOrStandard.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setMethStand(methodOrStandard[i]);
        }

        return dataSamplePayloads;
    };

    private static final Supplier<DataSamplePayload> INVALID_METHOD_OR_STANDARD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setMethStand("Not a recognized standard");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_REFERENCE_PERIOD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setReferencePeriod("Not a reference period");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload[]> VALID_VALUE_FORMATS = () -> {
        String[] validValueFields = new String[] {
                "123", "123.456", "<123", ">123", "-123", "0.23", "-0.23", ">-1.23", "<-123.456"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validValueFields.length];

        for (int i = 0; i < validValueFields.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setValue(validValueFields[i]);
        }

        return dataSamplePayloads;
    };

    private static final Supplier<DataSamplePayload[]> INVALID_VALUE_FORMATS = () -> {
        String[] invalidValueFields = new String[] { "abc", "1a23", "1#23" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[invalidValueFields.length];

        for (int i = 0; i < invalidValueFields.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setValue(invalidValueFields[i]);
        }

        return dataSamplePayloads;
    };

    private static final Supplier<DataSamplePayload> NO_VALUE_OR_TEXT_VALUE = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setValue(null);
        dataSamplePayload.setTextValue(null);
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> VALUE_AND_TEXT_VALUE = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setTextValue("No result");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> INVALID_TEXT_VALUE = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setTextValue("Not a text value");
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> EMBEDDED_SEPARATORS = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload(REQUIRED_FIELDS.get());
        dataSamplePayload.setParameter("1,2,3,6,7,8-Hexachlorodibenzofuran");
        return dataSamplePayload;
    };

    private final static Supplier<DataSamplePayload[]> VALID_TEXT_VALUES = () -> {
        String[] validTextValueFields = new String[] { "no", "TRUE", "false" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validTextValueFields.length];

        for (int i = 0; i < validTextValueFields.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setTextValue(validTextValueFields[i]);
            dataSamplePayloads[i].setValue(null);
            dataSamplePayloads[i].setUnit(null);
        }

        return dataSamplePayloads;
    };

    private final static Supplier<DataSamplePayload[]> VALID_RETURN_TYPES = () -> {
        String[] returnTypes = new String[] { "Landfill gas borehole", "Landfill  Gas  Engine" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[returnTypes.length];

        for (int i = 0; i < returnTypes.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setReturnType(returnTypes[i]);
        }

        return dataSamplePayloads;
    };

    private final static Supplier<DataSamplePayload[]> VALID_PARAMETERS = () -> {
        String[] validParameters = new String[] {
                "1,2,3,4,7,8-Hexachlorodibenzofuran",
                "1,3,5-Trinitrobenzene",
                "1,4-Benzenedicarboxylic acid,  dimethyl ester",
                "2,2',3,5,5',6-HexaCHLorobiphenyl",
                "2,2',4,5-Tetrachlorobiphenyl"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validParameters.length];

        for (int i = 0; i < validParameters.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setParameter(validParameters[i]);
        }

        return dataSamplePayloads;
    };

    private final static Supplier<DataSamplePayload[]> VALID_UNITS = () -> {
        String[] validUnits = new String[] { "µg/m²", "µScm-1", "cm³", "tSO2/GWh", "tSO²/GWh" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validUnits.length];

        for (int i = 0; i < validUnits.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setUnit(validUnits[i]);
        }

        return dataSamplePayloads;
    };

    private final static Supplier<DataSamplePayload[]> VALID_RETURN_PERIODS = () -> {
        String[] validReturnPeriods = new String[] { "Sep 2016", "Water year 2016" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validReturnPeriods.length];

        for (int i = 0; i < validReturnPeriods.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setReturnPeriod(validReturnPeriods[i]);
        }

        return dataSamplePayloads;
    };

    private final static Supplier<DataSamplePayload[]> VALID_REFERENCE_PERIODS = () -> {
        String[] validReferencePeriods = new String[] { "Daily average", "Calendar Monthly  Mean",
                "95% of all 10-minute averages in any 24 hour period",
                "Periodic over minimum 30 minute, maximum 8 hour period", "24 hour total", "24 HOUR TOTAL" };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[validReferencePeriods.length];

        for (int i = 0; i < validReferencePeriods.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS.get());
            dataSamplePayloads[i].setReferencePeriod(validReferencePeriods[i]);
        }

        return dataSamplePayloads;
    };

    private final static ResourceIntegrationTestExpectations PASS_WITH_NO_ERRORS = (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
            && t.getConstraintDefinitions().size() == 0
            && t.isValid == true;

    private final static ResourceIntegrationTestExpectations MULTI_PASS_WITH_NO_ERRORS = (t) -> t.getHttpStatus().equals(HttpStatus.MULTI_STATUS)
            && t.getConstraintDefinitions().size() == 0
            && t.getHttpMultiStatus().containsAll(Arrays.asList(HTTP_1_1_201_CREATED))
            && t.isValid == true;

    // Initialize the map containing each test and the expected results
    static {
        Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> singleRecordResourceTests = new LinkedHashMap<>();
        Map<String, Pair<DataSamplePayload[], ResourceIntegrationTestExpectations>> multiRecordResourceTests = new LinkedHashMap<>();

        singleRecordResourceTests.put("Empty payload generates violations", new ImmutablePair<>(EMPTY_PAYLOAD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                    && t.getConstraintDefinitions().containsAll(Arrays.asList(
                        "DR9000-Missing",       // EA_ID Missing
                        "DR9010-Missing",       // Return Type Missing
                        "DR9030-Missing",       // Parameter Missing
                        "DR9110-Missing",       // Site name Missing
                        "DR9999-Missing",       // One of value or Txt Value
                        "DR9020-Missing",       // Monitoring Date
                        "DR9060-Missing"))      // Monitoring point
                    && t.getConstraintDefinitions().size() == 7
                    && t.isValid == false));

        singleRecordResourceTests.put("Required fields only", new ImmutablePair<>(REQUIRED_FIELDS.get(), PASS_WITH_NO_ERRORS));

        multiRecordResourceTests.put("Valid date formats", new ImmutablePair<>(SUPPORTED_DATE_FORMATS.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.MULTI_STATUS)
                        && t.getConstraintDefinitions().size() == 0
                        && t.getHttpMultiStatus().size() == 9
                        && t.getHttpMultiStatus().containsAll(Arrays.asList(HTTP_1_1_201_CREATED))
                        && t.isValid == true));

        singleRecordResourceTests.put("Permit not found", new ImmutablePair<>(PERMIT_NOT_FOUND.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Permit site mismatch", new ImmutablePair<>(PERMIT_SITE_MISMATCH.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Invalid permit", new ImmutablePair<>(INVALID_PERMIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Missing"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid return types", new ImmutablePair<>(VALID_RETURN_TYPES.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid return type", new ImmutablePair<>(INVALID_RETURN_TYPE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9010-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Missing return type", new ImmutablePair<>(MISSING_RETURN_TYPE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9010-Missing"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid return types", new ImmutablePair<>(VALID_PARAMETERS.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid parameter", new ImmutablePair<>(INVALID_PARAMETER.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9030-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Missing parameter", new ImmutablePair<>(MISSING_PARAMETER.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9030-Missing"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid return types", new ImmutablePair<>(VALID_UNITS.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Missing unit", new ImmutablePair<>(MISSING_UNIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9050-Missing"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Invalid unit", new ImmutablePair<>(INVALID_UNIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9050-Incorrect"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid return periods", new ImmutablePair<>(VALID_RETURN_PERIODS.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid return period", new ImmutablePair<>(INVALID_RETURN_PERIOD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9070-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Missing monitoring point", new ImmutablePair<>(MISSING_MONITORING_POINT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9060-Missing"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Monitoring point too long", new ImmutablePair<>(LONG_MONITORING_POINT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9060-Length"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid qualifiers", new ImmutablePair<>(VALID_QUALIFIERS.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid qualifier", new ImmutablePair<>(INVALID_QUALIFIER.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9180-Incorrect"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid reference periods periods", new ImmutablePair<>(VALID_REFERENCE_PERIODS.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid reference period", new ImmutablePair<>(INVALID_REFERENCE_PERIOD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9090-Incorrect"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid method or standard", new ImmutablePair<>(VALID_METHOD_OR_STANDARD.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid method or standard", new ImmutablePair<>(INVALID_METHOD_OR_STANDARD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9100-Incorrect"))
                        && t.isValid == false));

        singleRecordResourceTests.put("No value or text value", new ImmutablePair<>(NO_VALUE_OR_TEXT_VALUE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9999-Missing"))
                        && t.isValid == false));

        singleRecordResourceTests.put("Value and text value", new ImmutablePair<>(VALUE_AND_TEXT_VALUE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 2 // Conflict on unit and conflict on value
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9999-Conflict", "DR9050-Conflict"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid text values", new ImmutablePair<>(VALID_TEXT_VALUES.get(), MULTI_PASS_WITH_NO_ERRORS));

        singleRecordResourceTests.put("Invalid text value", new ImmutablePair<>(INVALID_TEXT_VALUE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9080-Incorrect"))
                        && t.isValid == false));

        multiRecordResourceTests.put("Valid value formats", new ImmutablePair<>(VALID_VALUE_FORMATS.get(), MULTI_PASS_WITH_NO_ERRORS));

        multiRecordResourceTests.put("Invalid value formats", new ImmutablePair<>(INVALID_VALUE_FORMATS.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.MULTI_STATUS)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9040-Incorrect"))
                        && t.getHttpMultiStatus().size() == 3
                        && t.getHttpMultiStatus().containsAll(Arrays.asList(HTTP_1_1_201_CREATED))
                        && t.isValid == false));

        singleRecordResourceTests.put("Embedded separators", new ImmutablePair<>(EMBEDDED_SEPARATORS.get(), PASS_WITH_NO_ERRORS));

        SINGLE_RECORD_RESOURCE_TESTS = Collections.unmodifiableMap(singleRecordResourceTests);
        MULTI_RECORD_RESOURCE_TESTS = Collections.unmodifiableMap(multiRecordResourceTests);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> singleRecordTestData() {

        List<String[]> singleRecordTestsNames = SINGLE_RECORD_RESOURCE_TESTS
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .map(p -> new String[]{p})
                .collect(Collectors.toList());

        List<String[]> multipleRecordTestsNames = MULTI_RECORD_RESOURCE_TESTS
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .map(p -> new String[]{p})
                .collect(Collectors.toList());

        List<String[]> result = new ArrayList<>();

        result.addAll(singleRecordTestsNames);
        result.addAll(multipleRecordTestsNames);

        return result;
    }

    public APIResourceIntegrationTests(String name) {
        this.name = name;
    }

    @Test
    public void runTests() {
        Dataset defaultDataset = getDefaultDataset();
        if (SINGLE_RECORD_RESOURCE_TESTS.containsKey(name)) {
            Pair<DataSamplePayload, ResourceIntegrationTestExpectations> test = SINGLE_RECORD_RESOURCE_TESTS.get(name);
            ResourceIntegrationTestExpectations resourceIntegrationTestExpectations = test.getRight();
            DataSamplePayload dataSamplePayload =  test.getLeft();

            ResourceIntegrationTestResult testResult = new ResourceIntegrationTestResult(defaultDataset,
                    submitPayload(defaultDataset, dataSamplePayload));

            Assert.assertTrue(name, resourceIntegrationTestExpectations.passes(testResult));
        } else if (MULTI_RECORD_RESOURCE_TESTS.containsKey(name)) {
            Pair<DataSamplePayload[], ResourceIntegrationTestExpectations> test = MULTI_RECORD_RESOURCE_TESTS.get(name);
            ResourceIntegrationTestExpectations resourceIntegrationTestExpectations = test.getRight();
            DataSamplePayload[] dataSamplePayloads =  test.getLeft();

            ResourceIntegrationTestResult testResult = new ResourceIntegrationTestResult(defaultDataset,
                    submitPayload(defaultDataset, dataSamplePayloads));

            Assert.assertTrue(name, resourceIntegrationTestExpectations.passes(testResult));
        }
    }

    private class ResourceIntegrationTestResult {
        private final boolean isValid;
        private List<String> violationsList;
        private HttpStatus httpStatus;
        private List<String> httpMultiStatus;

        private ResourceIntegrationTestResult(
                Dataset dataset,
                ResponseEntity<? extends ResponseWrapper<?>> responseEntity) {

            // Get the top level http status code
            httpStatus = responseEntity.getStatusCode();

            // Get the dataset status
            ResponseEntity<DatasetStatusResponse> datasetResponseEntity = datasetRequest(HttpStatus.OK)
                    .getStatus(dataset.getId());

            DatasetStatus datasetStatus = datasetResponseEntity.getBody().getData();
            this.isValid = datasetStatus.getValidity().isValid();

            // Get string list of violation
            violationsList = datasetStatus
                    .getValidity()
                    .getViolations()
                    .stream()
                    .map(DatasetValidity.Violation::getConstraint)
                    .map(EntityReference::getId)
                    .collect(Collectors.toList());

            // Process the record level responses
            ResponseWrapper<?> responseBody = responseEntity.getBody();

            // If its a multiple then make the multi-status available
            if (responseBody instanceof MultiStatusResponse) {
                MultiStatusResponse multiStatusResponse = (MultiStatusResponse) responseBody;
                httpMultiStatus = multiStatusResponse
                        .getData()
                        .stream()
                        .map(MultiStatusResponse.Response::getStatus)
                        .collect(Collectors.toList());
            }
         }

        private List<String> getConstraintDefinitions() {
            return violationsList;
        }
        private HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public List<String> getHttpMultiStatus() {
            return httpMultiStatus;
        }
    }

    private final ResponseEntity<RecordEntityResponse> submitPayload(Dataset dataset, DataSamplePayload payload) {
        String recordId = UUID.randomUUID().toString();
        ResponseEntity<RecordEntityResponse> createResponse = recordRequest(HttpStatus.CREATED)
                .putRecord(dataset.getId(), recordId, payload);

        return createResponse;
    }

    private final ResponseEntity<MultiStatusResponse> submitPayload(Dataset dataset, DataSamplePayload[] payloads) {
        BatchRecordRequest request = new BatchRecordRequest();

        List<BatchRecordRequestItem> batchRecordRequestItems = new ArrayList<>();
        for (DataSamplePayload dataSamplePayload : payloads) {
            BatchRecordRequestItem batchRecordRequestItem = new BatchRecordRequestItem();
            batchRecordRequestItem.setRecordId(UUID.randomUUID().toString());
            batchRecordRequestItem.setPayload(dataSamplePayload);
            batchRecordRequestItems.add(batchRecordRequestItem);
        }

        request.setRequests(batchRecordRequestItems);

        ResponseEntity<MultiStatusResponse> createResponse = recordRequest(HttpStatus.MULTI_STATUS)
                .postRecords(dataset.getId(), request);

        return createResponse;
    }

    private final Dataset getDefaultDataset() {
        ResponseEntity<DatasetEntityResponse> response = createTestDataset();
        DatasetEntityResponse body = response.getBody();
        return body.getData();
    }

}
