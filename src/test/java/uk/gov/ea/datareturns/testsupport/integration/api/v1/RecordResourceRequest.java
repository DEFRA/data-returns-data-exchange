package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.RecordResource;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 18/05/17.
 */
public class RecordResourceRequest extends AbstractResourceRequest {
    public RecordResourceRequest(RestfulTest testClass, HttpStatus expected) {
        super(testClass, expected);
    }

    public ResponseEntity<EntityListResponse> listRecords() {
        return get(uri(RecordResource.class, "listRecords"), null, EntityListResponse.class);
    }

    public ResponseEntity<RecordEntityResponse> getRecord(String datasetId, String recordId) {
        return get(uri(RecordResource.class, "getRecord", templateValues(datasetId, recordId)), null, RecordEntityResponse.class);
    }

    public ResponseEntity<RecordEntityResponse> putRecord(String datasetId, String recordId, Payload payload) {
        URI uri = uri(RecordResource.class, "putRecord", templateValues(datasetId, recordId));
        ResponseEntity<RecordEntityResponse> response = put(uri, payload, RecordEntityResponse.class);
        if (getExpected().is2xxSuccessful()) {
            Record data = response.getBody().getData();
            Assert.assertEquals(recordId, data.getId());
        }
        return response;
    }

    public ResponseEntity<MultiStatusResponse> postRecords(String datasetId, BatchRecordRequest request) {
        return postBatchRequest(uri(RecordResource.class, "postRecords", templateValues(datasetId)), request);
    }

    public ResponseEntity<?> deleteRecord(String datasetId, String recordId) {
        URI uri = uri(RecordResource.class, "deleteRecord", templateValues(datasetId, recordId));
        return delete(uri);
    }

    private Map<String, Object> templateValues(String datasetId, String recordId) {
        Map<String, Object> values = templateValues(datasetId);
        values.put("record_id", recordId);
        return values;
    }

    private Map<String, Object> templateValues(String datasetId) {
        Map<String, Object> values = new HashMap<>();
        values.put("dataset_id", datasetId);
        return values;
    }

    @Override public RecordResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }
}
