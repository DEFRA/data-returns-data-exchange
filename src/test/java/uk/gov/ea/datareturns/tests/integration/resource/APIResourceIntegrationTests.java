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
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.ConstraintDefinition;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

import java.util.*;
import java.util.function.Supplier;

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
        dataSamplePayload.setEaId("TS1234TS");
        //dataSamplePayload.setPayloadType(PAYLOAD_TYPE);
        return dataSamplePayload;
    };

    private static final List<ConstraintDefinition> EMPTY_CONSTRAINT_LIST = Collections.EMPTY_LIST;

    private final static Map<ResourceIntegrationTestResult, DataSamplePayload> RESOURCE_TESTS = ImmutableMap.of(

            // Create a record with an empty payload
            (t) -> t.getHttpStatus().equals(HttpStatus.CREATED) &&
                    t.getConstraintDefinitions().containsAll(EMPTY_CONSTRAINT_LIST),

            EMPTY_PAYLOAD.get()

    );

    @Test
    public void runTests() {
        Dataset defaultDataset = getDefaultDataset();
        for (Map.Entry<ResourceIntegrationTestResult, DataSamplePayload> es : RESOURCE_TESTS.entrySet()) {
            ResourceIntegrationTestConditions testResult = new ResourceIntegrationTestConditions(submitPayload(defaultDataset, es.getValue()));
            Assert.assertTrue(es.getKey().passes(testResult));
        }
    }

    @FunctionalInterface
    public interface ResourceIntegrationTestResult {
        boolean passes(ResourceIntegrationTestConditions t);
    }

    private class ResourceIntegrationTestConditions {
        private List<ConstraintDefinition> constraintDefinitions;
        private HttpStatus httpStatus;

        private ResourceIntegrationTestConditions(ResponseEntity<RecordEntityResponse> recordEntityResponseResponseEntity) {
            httpStatus = recordEntityResponseResponseEntity.getStatusCode();
            constraintDefinitions = null;//TODO figure out best way here - do we just request the status?
        }

        private List<ConstraintDefinition> getConstraintDefinitions() {
            return constraintDefinitions;
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
