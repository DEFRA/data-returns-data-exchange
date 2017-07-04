package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
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
import java.util.logging.Logger;
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
    public static final SpringClassRule SPRING_CLASS_RULE= new SpringClassRule();

    @Rule
    public final SpringMethodRule  springMethodRule = new SpringMethodRule();

    private static final Supplier<DataSamplePayload> EMPTY_PAYLOAD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload();
        return dataSamplePayload;
    };

    private static final Supplier<DataSamplePayload> REQUIRED_FIELDS_PAYLOAD = () -> {
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
                "2016-04-15",
                "2016-04-16T09:04:59",
                "2016-04-16 09:04:59",
                "17-04-2016",
                "17-04-2016T09:04:59",
                "17-04-2016 09:04:59",
                "17/04/2016",
                "17/04/2016T09:04:59",
                "17/04/2016 09:04:59"
        };

        DataSamplePayload[] dataSamplePayloads = new DataSamplePayload[testDateFormats.length];

        for (int i = 0; i < testDateFormats.length; i++) {
            dataSamplePayloads[i] = new DataSamplePayload(REQUIRED_FIELDS_PAYLOAD.get());
            dataSamplePayloads[i].setMonitoringDate(testDateFormats[i]);
        }

        return dataSamplePayloads;
    };

    private static final Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> RESOURCE_TESTS;

    static {
        Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> resourceTests = new LinkedHashMap<>();

        resourceTests.put("Empty Payload generates violations", new ImmutablePair<>(EMPTY_PAYLOAD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                    && t.getConstraintDefinitions().containsAll(Arrays.asList(
                    "DR9000-Missing",   // EA_ID Missing
                    "DR9010-Missing",       // Return Type Missing
                    "DR9030-Missing",       // Parameter Missing
                    "DR9110-Missing",       // Site name Missing
                    "DR9999-Missing",       // One of value or Txt Value
                    "DR9020-Missing",       // Monitoring Date
                    "DR9060-Missing"))      // Monitoring point
                    && t.getConstraintDefinitions().size() == 7
                    && t.isValid == false));

        resourceTests.put("Required fields only", new ImmutablePair<>(REQUIRED_FIELDS_PAYLOAD.get(),
                (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                        && t.getConstraintDefinitions().size() == 0
                        && t.isValid == true));

        for(DataSamplePayload dataSamplePayload : SUPPORTED_DATE_FORMATS.get()) {
            resourceTests.put("Accepts date format: " + dataSamplePayload.getMonitoringDate(), new ImmutablePair<>(dataSamplePayload,
                    (t) -> t.getHttpStatus().equals(HttpStatus.CREATED)
                            && t.getConstraintDefinitions().size() == 0
                            && t.isValid == true));

        }

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

    // Will convert to use parametrized tests
    @Test
    public void runSingleNamedTest() {
        Dataset defaultDataset = getDefaultDataset();

        //String name = "Required fields only";
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
