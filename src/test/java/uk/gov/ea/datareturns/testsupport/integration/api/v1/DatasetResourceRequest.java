package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.DatasetResource;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.multistatus.MultiStatusResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 18/05/17.
 */
public class DatasetResourceRequest extends AbstractResourceRequest {
    public DatasetResourceRequest(RestfulTest testClass, HttpStatus expected) {
        super(testClass, expected);
    }

    public ResponseEntity<EntityListResponse> listDatasets() {
        return get(uri(DatasetResource.class, "listDatasets"), null, EntityListResponse.class);
    }

    public ResponseEntity<DatasetEntityResponse> getDataset(String datasetId) {
        return get(uri(DatasetResource.class, "getDataset", templateValues(datasetId)), null, DatasetEntityResponse.class);
    }

    public ResponseEntity<DatasetEntityResponse> putDataset(String datasetId, DatasetProperties properties) {
        URI uri = uri(DatasetResource.class, "putDataset", templateValues(datasetId));
        ResponseEntity<DatasetEntityResponse> response = put(uri, properties, DatasetEntityResponse.class);
        if (getExpected().is2xxSuccessful()) {
            Dataset data = response.getBody().getData();
            Assert.assertEquals(datasetId, data.getId());
        }
        return response;
    }

    public ResponseEntity<MultiStatusResponse> postDatasets(BatchDatasetRequest request) {
        return postBatchRequest(uri(DatasetResource.class, "postDatasets"), request);
    }

    public ResponseEntity<?> deleteDataset(String datasetId) {
        URI uri = uri(DatasetResource.class, "deleteDataset", templateValues(datasetId));
        return delete(uri);
    }

    private Map<String, Object> templateValues(String datasetId) {
        Map<String, Object> values = new HashMap<>();
        values.put("dataset_id", datasetId);
        return values;
    }

    @Override public DatasetResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }
}
