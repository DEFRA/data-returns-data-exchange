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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    //private final static String PAYLOAD_TYPE = "DataSamplePayload";

    //private static final String REQUIRED_FILEDS_ONLY = "REQUIRED_FILEDS_ONLY";
    //private Map<String, ResourceIntegrationTestArtifacts> resourceIntegrationTestArtifactsMap =
    //        ImmutableMap.of(
    //                REQUIRED_FILEDS_ONLY, new ResourceIntegrationTestArtifacts(
    //                        HttpStatus.CREATED,
    //                        pl -> { return new DataSamplePayload(); },
    //                        new ArrayList<ConstraintDefinition>()
    //                )
    //        );

    private static NewDataSamplePayload xxxx = () -> new DataSamplePayload();

    private final static Map<ResourceIntegrationTestResult, DataSamplePayload> RESOURCE_TESTS = ImmutableMap.of(

            // Create a record with an empty payload
            (t) -> t.getHttpStatus().equals(HttpStatus.CREATED) &&
                    t.getConstraintDefinitions().containsAll(new ArrayList<ConstraintDefinition>()), xxxx.create()

    );

    @Test
    public void doit() {
        NewDataSamplePayload xxxx = () -> new DataSamplePayload();


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

    @FunctionalInterface
    public interface NewDataSamplePayload {
        DataSamplePayload create();
    }





    public class ResourceIntegrationTestConditions {
        private List<ConstraintDefinition> constraintDefinitions;
        private HttpStatus httpStatus;

        public ResourceIntegrationTestConditions(ResponseEntity<RecordEntityResponse> recordEntityResponseResponseEntity) {
            httpStatus = recordEntityResponseResponseEntity.getStatusCode();
            constraintDefinitions = null;//TODO figure out best way here - do we just request the status?
        }

        public void setHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        public List<ConstraintDefinition> getConstraintDefinitions() {
            return constraintDefinitions;
        }

        public void setConstraintDefinitions(List<ConstraintDefinition> constraintDefinitions) {
            this.constraintDefinitions = constraintDefinitions;
        }
        public HttpStatus getHttpStatus() {
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
