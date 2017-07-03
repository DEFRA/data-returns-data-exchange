package uk.gov.ea.datareturns.tests.integration.resource;

import org.influxdb.com.google.guava.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.ConstraintDefinition;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class APIResourceIntegrationTests extends AbstractDataResourceTests {

    //public static final String PAYLOAD_TYPE = "DataSamplePayload";

    private static final Supplier<DataSamplePayload> EMPTY_PAYLOAD = () -> {
        DataSamplePayload dataSamplePayload = new DataSamplePayload();
        return dataSamplePayload;
    };

    private static final List<String> EMPTY_CONSTRAINT_LIST = Collections.EMPTY_LIST;
    private static final Map<ResourceIntegrationTestExpectations, DataSamplePayload> RESOURCE_TESTS;

    static {
        Map<ResourceIntegrationTestExpectations, DataSamplePayload> resourceTests = new HashMap<>();

        resourceTests.put(
                // Create a record with an empty payload
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
                        && t.isValid == false,

                EMPTY_PAYLOAD.get()
        );

        RESOURCE_TESTS = Collections.unmodifiableMap(resourceTests);
    }

    @Test
    public void runTests() {
        Dataset defaultDataset = getDefaultDataset();
        for (Map.Entry<ResourceIntegrationTestExpectations, DataSamplePayload> es : RESOURCE_TESTS.entrySet()) {

            ResourceIntegrationTestResult testResult = new ResourceIntegrationTestResult(defaultDataset,
                    submitPayload(defaultDataset, es.getValue()));

            Assert.assertTrue(es.getKey().passes(testResult));
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

        public boolean isValid() {
            return isValid;
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
