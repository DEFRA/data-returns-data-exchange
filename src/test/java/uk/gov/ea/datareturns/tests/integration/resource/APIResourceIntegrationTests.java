package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class APIResourceIntegrationTests extends AbstractDataResourceTests {

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

    private static final Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> RESOURCE_TESTS;

    static {
        Map<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> resourceTests = new HashMap<>();

        resourceTests.put("Empty Payload", new ImmutablePair<>(EMPTY_PAYLOAD.get(),
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

        RESOURCE_TESTS = Collections.unmodifiableMap(resourceTests);
    }

    @Test
    public void runTests() {
        Dataset defaultDataset = getDefaultDataset();
        for (Map.Entry<String, Pair<DataSamplePayload, ResourceIntegrationTestExpectations>> es : RESOURCE_TESTS.entrySet()) {
            String description = es.getKey();
            ResourceIntegrationTestExpectations resourceIntegrationTestExpectations = es.getValue().getRight();
            DataSamplePayload dataSamplePayload =  es.getValue().getLeft();

            ResourceIntegrationTestResult testResult = new ResourceIntegrationTestResult(defaultDataset,
                    submitPayload(defaultDataset, dataSamplePayload));

            Assert.assertTrue(description, resourceIntegrationTestExpectations.passes(testResult));
        }
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

    private ResponseEntity<RecordEntityResponse> submitPayload(Dataset dataset, DataSamplePayload payload) {
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
