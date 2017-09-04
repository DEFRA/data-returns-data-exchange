package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

import java.util.*;
import java.util.function.Function;

/**
 * RecordResource tests
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class RecordResourceTests extends AbstractDataResourceTests {

    @Test
    public void testGetRequestIfNoneMatchPasses() {
        executeConditionalGetRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfNoneMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testGetRequestIfNoneMatchFails() {
        executeConditionalGetRequestTest(HttpStatus.NOT_MODIFIED, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfNoneMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testGetRequestIfNoneMatchWildFails() {
        executeConditionalGetRequestTest(HttpStatus.NOT_MODIFIED, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfNoneMatch("*");
            return headers;
        });
    }

    @Test
    public void testGetRequestIfModifiedSincePasses() {
        executeConditionalGetRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfModifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    @Test
    public void testGetRequestIfModifiedSinceFails() {
        executeConditionalGetRequestTest(HttpStatus.NOT_MODIFIED, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfModifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfMatchSucceeds() {
        executeConditionalPutRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update datasetRequest headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfMatchWildSucceeds() {
        executeConditionalPutRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update datasetRequest headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("*");
            return headers;
        });
    }

    @Test
    public void testPutRequestIfMatchFails() {
        executeConditionalPutRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the update datasetRequest header to use a rubbish etag
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testPutRequestIfUnmodifiedSinceSucceeds() {
        executeConditionalPutRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfUnmodifiedSinceFails() {
        executeConditionalPutRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the update datasetRequest headers to be before the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfMatchSucceeds() {
        executeConditionalDeleteRequestTest(HttpStatus.NO_CONTENT, (createResponse) -> {
            // Set the delete datasetRequest headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfMatchWildSucceeds() {
        executeConditionalDeleteRequestTest(HttpStatus.NO_CONTENT, (createResponse) -> {
            // Set the delete datasetRequest headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("*");
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfMatchFails() {
        executeConditionalDeleteRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the delete datasetRequest header to use a rubbish etag
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfUnmodifiedSinceSucceeds() {
        executeConditionalDeleteRequestTest(HttpStatus.NO_CONTENT, (createResponse) -> {
            // Set the delete datasetRequest headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfUnmodifiedSinceFails() {
        executeConditionalDeleteRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the delete datasetRequest headers to be before the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    @Test
    public void testPostRequestIfMatchSucceeds() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        Map<String, MultiStatusResponse.Response> records = executeBatchCreateRequestTest(dataset);

        executeBatchUpdateRequestTest(dataset, records, (recordId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch(records.get(recordId).getEntityTag());
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestDuplicateIdentifiers() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        executeBatchDuplicateRecordTest(dataset);
    }

    @Test
    public void testPostRequestIfMatchWildSucceeds() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        Map<String, MultiStatusResponse.Response> records = executeBatchCreateRequestTest(dataset);

        executeBatchUpdateRequestTest(dataset, records, (recordId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch("*");
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestIfMatchFails() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        Map<String, MultiStatusResponse.Response> records = executeBatchCreateRequestTest(dataset);

        // Now for each dataset, attempt an update with If-Match set to an invalid
        executeBatchUpdateRequestTest(dataset, records, (recordId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return Pair.of(preconditions, HttpStatus.PRECONDITION_FAILED);
        });
    }

    @Test
    public void testPostRequestIfUnmodifiedSinceSucceeds() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        Map<String, MultiStatusResponse.Response> records = executeBatchCreateRequestTest(dataset);

        executeBatchUpdateRequestTest(dataset, records, (recordId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfUnmodifiedSince(records.get(recordId).getLastModified());
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestIfUnmodifiedSinceFails() {
        // Issue a batch POST datasetRequest to create initial data
        Dataset dataset = createTestDataset(TestPermitData.getTestData()[0].uniqueId).getBody().getData();
        Map<String, MultiStatusResponse.Response> records = executeBatchCreateRequestTest(dataset);

        executeBatchUpdateRequestTest(dataset, records, (recordId) -> {
            Date lastModified = records.get(recordId).getLastModified();
            Date beforeLastModified = new Date(lastModified.getTime() - 1000);

            Preconditions preconditions = new Preconditions();
            preconditions.setIfUnmodifiedSince(beforeLastModified);
            return Pair.of(preconditions, HttpStatus.PRECONDITION_FAILED);
        });
    }

    private ResponseEntity<RecordEntityResponse> createTestRecord(Dataset dataset) {
        String recordId = UUID.randomUUID().toString();

        DataSamplePayload payload = new DataSamplePayload();
        payload.setEaId("TS1234TS");

        ResponseEntity<RecordEntityResponse> createResponse = recordRequest(HttpStatus.CREATED)
                .putRecord(TestPermitData.getTestData()[0].uniqueId, dataset.getId(), recordId, payload);

        Record record = createResponse.getBody().getData();
        Assert.assertEquals(recordId, record.getId());
        Assert.assertNotNull(record.getCreated());
        Assert.assertNotNull(record.getLastModified());
        Assert.assertTrue(record.getPayload() instanceof DataSamplePayload);

        DataSamplePayload savedPayload = (DataSamplePayload) record.getPayload();
        Assert.assertEquals("TS1234TS", savedPayload.getEaId());
        return createResponse;
    }

    private void executeConditionalGetRequestTest(HttpStatus expected,
            Function<ResponseEntity<RecordEntityResponse>, HttpHeaders> getRequestHeaderProvider) {
        // First PUT a new dataset and add a record
        ResponseEntity<DatasetEntityResponse> createDatasetResponse =
                createTestDataset(TestPermitData.getTestData()[0].uniqueId);
        Dataset dataset = createDatasetResponse.getBody().getData();

        ResponseEntity<RecordEntityResponse> createRecordResponse = createTestRecord(dataset);
        Record record = createRecordResponse.getBody().getData();

        // Now attempt to get the same dataset using a conditional datasetRequest
        HttpHeaders headers = getRequestHeaderProvider.apply(createRecordResponse);
        ResponseEntity<RecordEntityResponse> getResponse = recordRequest(expected)
                .withHeaders(headers)
                .getRecord(TestPermitData.getTestData()[0].uniqueId, dataset.getId(), record.getId());

        if (expected == HttpStatus.NOT_MODIFIED) {
            Assert.assertNull(getResponse.getBody());
        } else {
            Assert.assertNotNull(getResponse.getBody());
        }
    }

    private void executeConditionalPutRequestTest(HttpStatus expected,
            Function<ResponseEntity<RecordEntityResponse>, HttpHeaders> updateRequestHeaderProvider) {
        // First PUT a new dataset and add a record
        ResponseEntity<DatasetEntityResponse> createDatasetResponse =
                createTestDataset(TestPermitData.getTestData()[0].uniqueId);

        Dataset dataset = createDatasetResponse.getBody().getData();

        ResponseEntity<RecordEntityResponse> createRecordResponse = createTestRecord(dataset);
        Record record = createRecordResponse.getBody().getData();

        // Now attempt to update the same dataset using a conditional datasetRequest and add properties
        HttpHeaders headers = updateRequestHeaderProvider.apply(createRecordResponse);

        // New payload to be saved
        DataSamplePayload newPayload = new DataSamplePayload();
        newPayload.setEaId("AB1234CD");

        ResponseEntity<RecordEntityResponse> updateResponse = recordRequest(expected)
                .withHeaders(headers)
                .putRecord(TestPermitData.getTestData()[0].uniqueId,
                        dataset.getId(), record.getId(), newPayload);

        if (updateResponse.getStatusCode().is2xxSuccessful()) {
            Record updatedRecord = updateResponse.getBody().getData();
            Assert.assertEquals(record.getId(), updatedRecord.getId());
            Assert.assertEquals(record.getCreated(), updatedRecord.getCreated());
            Assert.assertNotEquals(record.getLastModified(), updatedRecord.getLastModified());

            Assert.assertTrue(updatedRecord.getPayload() instanceof DataSamplePayload);

            DataSamplePayload updatedPayload = (DataSamplePayload) updatedRecord.getPayload();
            Assert.assertEquals("AB1234CD", updatedPayload.getEaId());
        } else {
            Assert.assertNull(updateResponse.getBody().getData());
        }
    }

    private void executeConditionalDeleteRequestTest(HttpStatus expected,
            Function<ResponseEntity<RecordEntityResponse>, HttpHeaders> deleteRequestHeaderProvider) {
        // First PUT a new dataset and add a record
        ResponseEntity<DatasetEntityResponse> createDatasetResponse =
                createTestDataset(TestPermitData.getTestData()[0].uniqueId);
        Dataset dataset = createDatasetResponse.getBody().getData();

        ResponseEntity<RecordEntityResponse> createRecordResponse = createTestRecord(dataset);
        Record record = createRecordResponse.getBody().getData();

        // Now attempt to delete the same dataset using a conditional datasetRequest
        HttpHeaders headers = deleteRequestHeaderProvider.apply(createRecordResponse);

        ResponseEntity<?> deleteResponse = recordRequest(expected)
                .withHeaders(headers)
                .deleteRecord(TestPermitData.getTestData()[0].uniqueId,
                        dataset.getId(), record.getId());
        if (deleteResponse.getStatusCode().is2xxSuccessful()) {
            Assert.assertNull(deleteResponse.getBody());

            // Check that the record has actually been deleted
            recordRequest(HttpStatus.NOT_FOUND).getRecord(
                    TestPermitData.getTestData()[0].uniqueId, dataset.getId(), record.getId());
        } else {
            Assert.assertNotNull(deleteResponse.getBody());
        }
    }

    private Map<String, MultiStatusResponse.Response> executeBatchCreateRequestTest(Dataset target) {
        int count = 10;
        Map<String, MultiStatusResponse.Response> records = new LinkedHashMap<>();

        // Issue a batch POST datasetRequest and record the response
        List<BatchRecordRequestItem> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final String recordId = "record_" + i;

            BatchRecordRequestItem request = new BatchRecordRequestItem();
            request.setRecordId(recordId);

            DataSamplePayload payload = new DataSamplePayload();
            payload.setEaId(recordId);
            payload.setComments(recordId);
            requests.add(request);
        }
        ResponseEntity<MultiStatusResponse> createResponse = recordRequest(HttpStatus.MULTI_STATUS)
                .postRecords(TestPermitData.getTestData()[0].uniqueId,
                        target.getId(), new BatchRecordRequest(requests));
        // Test the response and store each one in the records map
        List<MultiStatusResponse.Response> responses = createResponse.getBody().getData();
        for (int i = 0; i < responses.size(); i++) {
            final MultiStatusResponse.Response response = responses.get(i);
            final String expectedRecordId = "record_" + i;

            Assert.assertEquals(HttpStatus.CREATED.value(), response.getCode());
            Assert.assertNotNull(response.getEntityTag());
            Assert.assertNotNull(response.getLastModified());
            Assert.assertNotNull(response.getHref());
            Assert.assertEquals(expectedRecordId, response.getId());

            records.put(response.getId(), response);
        }
        return records;
    }

    private Map<String, MultiStatusResponse.Response> executeBatchDuplicateRecordTest(Dataset target) {
        String[] recordIds = new String[] { "a", "b", "c", "b" };
        Map<String, MultiStatusResponse.Response> records = new LinkedHashMap<>();

        // Issue a batch POST datasetRequest and record the response
        List<BatchRecordRequestItem> requests = new ArrayList<>();
        for (String recordId : recordIds) {

            BatchRecordRequestItem request = new BatchRecordRequestItem();
            request.setRecordId(recordId);

            DataSamplePayload payload = new DataSamplePayload();
            payload.setEaId(recordId);
            payload.setComments(recordId);
            requests.add(request);
        }

        recordRequest(HttpStatus.CONFLICT)
                .postRecords(TestPermitData.getTestData()[0].uniqueId, target.getId(), new BatchRecordRequest(requests));

        return records;
    }

    private ResponseEntity<MultiStatusResponse> executeBatchUpdateRequestTest(Dataset target, Map<String, MultiStatusResponse.Response>
            originalRecords,
            Function<String, Pair<Preconditions, HttpStatus>> preconditionProvider) {

        // Map of expected response codes for each id
        Map<String, HttpStatus> expectations = new HashMap<>();

        // Now for each dataset, attempt an update with If-Match set to the proper value
        List<BatchRecordRequestItem> requests = new ArrayList<>();
        for (String id : originalRecords.keySet()) {
            BatchRecordRequestItem request = new BatchRecordRequestItem();
            request.setRecordId(id);

            Pair<Preconditions, HttpStatus> conditionPair = preconditionProvider.apply(id);
            request.setPreconditions(conditionPair.getLeft());
            expectations.put(id, conditionPair.getRight());

            DataSamplePayload updatedPayload = new DataSamplePayload();
            updatedPayload.setEaId("Updated");
            updatedPayload.setComments("Updated");
            request.setPayload(updatedPayload);

            requests.add(request);
        }

        ResponseEntity<MultiStatusResponse> responses = recordRequest(HttpStatus.MULTI_STATUS)
                .postRecords(TestPermitData.getTestData()[0].uniqueId,
                        target.getId(), new BatchRecordRequest(requests));

        responses.getBody().getData().forEach((response) -> {
            HttpStatus expected = expectations.get(response.getId());
            HttpStatus status = HttpStatus.valueOf(response.getCode());

            // Response at creation time
            MultiStatusResponse.Response createResponse = originalRecords.get(response.getId());

            // Common assertions regardless of http status
            Assert.assertEquals(expected, status);
            Assert.assertEquals(createResponse.getId(), response.getId());
            Assert.assertEquals(createResponse.getHref(), response.getHref());

            if (status == HttpStatus.OK) {
                Assert.assertNotEquals(createResponse.getEntityTag(), response.getEntityTag());
                Assert.assertNotEquals(createResponse.getLastModified(), response.getLastModified());
            } else if (status != HttpStatus.PRECONDITION_FAILED) {
                Assert.fail("Unexpected response");
            }
        });
        return responses;
    }
}