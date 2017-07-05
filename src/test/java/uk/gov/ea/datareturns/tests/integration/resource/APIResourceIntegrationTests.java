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
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

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

    // An ordered map of all the resource tests to be run in turned
    private static final Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> RESOURCE_TESTS;

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

    // Initialize the map containing each test and the expected results
    static {
        Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> resourceTests = new LinkedHashMap<>();

        resourceTests.put("Empty payload generates violations", new ImmutablePair<>(EMPTY_PAYLOAD.get(),
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

        resourceTests.put("Required fields only", new ImmutablePair<>(REQUIRED_FIELDS.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 0
                        && t.isValid == true));

        for(DataSamplePayload dataSamplePayload : SUPPORTED_DATE_FORMATS.get()) {
            resourceTests.put("Accepts date format: " + dataSamplePayload.getMonitoringDate(), new ImmutablePair<>(dataSamplePayload,
                    (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                            && t.getConstraintDefinitions().size() == 0
                            && t.isValid == true));
        }

        resourceTests.put("Permit not found", new ImmutablePair<>(PERMIT_NOT_FOUND.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Permit site mismatch", new ImmutablePair<>(PERMIT_SITE_MISMATCH.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Invalid permit", new ImmutablePair<>(INVALID_PERMIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9000-Missing"))
                        && t.isValid == false));

        resourceTests.put("Invalid return type", new ImmutablePair<>(INVALID_RETURN_TYPE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9010-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Missing return type", new ImmutablePair<>(MISSING_RETURN_TYPE.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9010-Missing"))
                        && t.isValid == false));

        resourceTests.put("Invalid parameter", new ImmutablePair<>(INVALID_PARAMETER.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9030-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Missing parameter", new ImmutablePair<>(MISSING_PARAMETER.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9030-Missing"))
                        && t.isValid == false));

        resourceTests.put("Missing unit", new ImmutablePair<>(MISSING_UNIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9050-Missing"))
                        && t.isValid == false));

        resourceTests.put("Invalid unit", new ImmutablePair<>(INVALID_UNIT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9050-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Invalid return period", new ImmutablePair<>(INVALID_RETURN_PERIOD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9070-Incorrect"))
                        && t.isValid == false));

        resourceTests.put("Missing monitoring point", new ImmutablePair<>(MISSING_MONITORING_POINT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9060-Missing"))
                        && t.isValid == false));

        resourceTests.put("Monitoring point too long", new ImmutablePair<>(LONG_MONITORING_POINT.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 1
                        && t.getConstraintDefinitions().containsAll(Arrays.asList("DR9060-Length"))
                        && t.isValid == false));

        RESOURCE_TESTS = Collections.unmodifiableMap(resourceTests);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> data() {
        List<String[]> parameters = RESOURCE_TESTS
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .map(p -> new String[]{p})
                .collect(Collectors.toList());
        return parameters;
    }

    public APIResourceIntegrationTests(String name) {
        this.name = name;
    }

    @Test
    public void runSingleNamedTest() {
        Dataset defaultDataset = getDefaultDataset();

        Pair<DataSamplePayload, ResourceIntegrationTestExpectations> test = RESOURCE_TESTS.get(name);

        ResourceIntegrationTestExpectations resourceIntegrationTestExpectations = test.getRight();
        DataSamplePayload dataSamplePayload =  test.getLeft();

        ResourceIntegrationTestResult testResult = new ResourceIntegrationTestResult(defaultDataset,
                submitPayload(defaultDataset, dataSamplePayload));

        Assert.assertTrue(name, resourceIntegrationTestExpectations.passes(testResult));
    }

    @FunctionalInterface
    private interface ResourceIntegrationTestExpectations {
        boolean passes(ResourceIntegrationTestResult t);
    }

    private class ResourceIntegrationTestResult {
        private final boolean isValid;
        private List<String> violationsList;
        private HttpStatus httpStatus;

        private ResourceIntegrationTestResult(
                Dataset dataset,
                ResponseEntity<RecordEntityResponse> recordEntityResponseResponseEntity) {

            httpStatus = recordEntityResponseResponseEntity.getStatusCode();

            // Get the dataset status
            ResponseEntity<DatasetStatusResponse> responseEntity = datasetRequest(HttpStatus.OK)
                    .getStatus(dataset.getId());

            DatasetStatus datasetStatus = responseEntity.getBody().getData();

            this.isValid = datasetStatus.getValidity().isValid();

            // Get string list of violation
            violationsList = datasetStatus
                    .getValidity()
                    .getViolations()
                    .stream()
                    .map(DatasetValidity.Violation::getConstraint)
                    .map(EntityReference::getId)
                    .collect(Collectors.toList());
        }

        private List<String> getConstraintDefinitions() {
            return violationsList;
        }
        private HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }

    private final ResponseEntity<RecordEntityResponse> submitPayload(Dataset dataset, DataSamplePayload payload) {
        String recordId = UUID.randomUUID().toString();
        ResponseEntity<RecordEntityResponse> createResponse = recordRequest(HttpStatus.CREATED)
                .putRecord(dataset.getId(), recordId, payload);

        return createResponse;
    }

    private final Dataset getDefaultDataset() {
        ResponseEntity<DatasetEntityResponse> response = createTestDataset();
        DatasetEntityResponse body = response.getBody();
        return body.getData();
    }
}
