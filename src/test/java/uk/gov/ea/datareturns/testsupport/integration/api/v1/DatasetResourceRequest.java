package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.DatasetResource;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;

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

    public ResponseEntity<EntityReferenceListResponse> listDatasets(String eaIdId) {
        return get(uri(DatasetResource.class, "listDatasets", templateValues(eaIdId)),
                null, EntityReferenceListResponse.class);
    }

    public ResponseEntity<DatasetEntityResponse> getDataset(String eaIdId, String datasetId) {
        return get(uri(DatasetResource.class, "getDataset", templateValues(eaIdId, datasetId)), null, DatasetEntityResponse.class);
    }

    public ResponseEntity<DatasetEntityResponse> putDataset(String eaIdId, String datasetId, DatasetProperties properties) {
        URI uri = uri(DatasetResource.class, "putDataset", templateValues(eaIdId, datasetId));
        ResponseEntity<DatasetEntityResponse> response = put(uri, properties, DatasetEntityResponse.class);
        if (getExpected().is2xxSuccessful()) {
            Dataset data = response.getBody().getData();
            Assert.assertEquals(datasetId, data.getId());
        }
        return response;
    }

    public ResponseEntity<MultiStatusResponse> postDatasets(String eaIdId, BatchDatasetRequest request) {
        return postBatchRequest(uri(DatasetResource.class, "postDatasets", templateValues(eaIdId)), request);
    }

    public ResponseEntity<?> deleteDataset(String eaIdId, String datasetId) {
        URI uri = uri(DatasetResource.class, "deleteDataset", templateValues(eaIdId, datasetId));
        return delete(uri);
    }

    private Map<String, Object> templateValues(String eaIdId) {
        Map<String, Object> values = new HashMap<>();
        values.put("ea_id", eaIdId);
        return values;
    }

    private Map<String, Object> templateValues(String eaIdId, String datasetId) {
        Map<String, Object> values = new HashMap<>();
        values.put("ea_id", eaIdId);
        values.put("dataset_id", datasetId);
        return values;
    }

    @Override public DatasetResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }

    public ResponseEntity<DatasetStatusResponse> getStatus(String eaIdId, String datasetId) {
        URI uri = uri(DatasetResource.class, "getDatasetStatus", templateValues(eaIdId, datasetId));
        return get(uri, null, DatasetStatusResponse.class);
    }
}
