package uk.gov.ea.datareturns.tests.integration.resource;

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
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

    private final static String PAYLOAD_TYPE = "DataSamplePayload";

    @Test
    public void checkDefaultDataset() {
        ResponseEntity<DatasetEntityResponse> response = createTestDataset();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testNullPayload() {
        ResponseEntity<RecordEntityResponse> recordEntityResponse =
                submitPayload(getDefaultDataset(), null);
        Record record = recordEntityResponse.getBody().getData();
        Assert.assertNull(record.getPayload());
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


    //curl -X GET "http://localhost:9020/api/v1/definitions/DataSamplePayload/fields" -H  "accept: application/json"
    //curl -X GET "http://localhost:9020/api/v1/definitions/DataSamplePayload/constraints" -H  "accept: application/json"
}
