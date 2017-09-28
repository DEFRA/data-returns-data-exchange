package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.RecordResource;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
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

    public ResponseEntity<RecordEntityResponse> getRecord(String eaIdId, String datasetId, String recordId) {
        return get(uri(RecordResource.class, "getRecord", templateValues(eaIdId, datasetId, recordId)), null, RecordEntityResponse.class);
    }

    public ResponseEntity<RecordEntityResponse> putRecord(String eaIdId, String datasetId, String recordId, Payload payload) {
        URI uri = uri(RecordResource.class, "putRecord", templateValues(eaIdId, datasetId, recordId));
        ResponseEntity<RecordEntityResponse> response = put(uri, payload, RecordEntityResponse.class);
        if (getExpected().is2xxSuccessful()) {
            Record data = response.getBody().getData();
            Assert.assertEquals(recordId, data.getId());
        }
        return response;
    }

    public ResponseEntity<MultiStatusResponse> postRecords(String eaIdId, String datasetId, BatchRecordRequest request) {
        return postBatchRequest(uri(RecordResource.class, "postRecords", templateValues(eaIdId, datasetId)), request);
    }

    public ResponseEntity<?> deleteRecord(String eaIdId, String datasetId, String recordId) {
        URI uri = uri(RecordResource.class, "deleteRecord", templateValues(eaIdId, datasetId, recordId));
        return delete(uri);
    }

    private Map<String, Object> templateValues(String eaIdId, String datasetId, String recordId) {
        Map<String, Object> values = templateValues(eaIdId, datasetId);
        values.put("record_id", recordId);
        return values;
    }

    private Map<String, Object> templateValues(String eaIdId, String datasetId) {
        Map<String, Object> values = new HashMap<>();
        values.put("ea_id", eaIdId);
        values.put("dataset_id", datasetId);
        return values;
    }

    @Override public RecordResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }
}
