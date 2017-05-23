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
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DatasetResource tests
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DatasetResourceTests extends  AbstractDataResourceTests {


    @Test
    public void testListDatasets() {
        // Add datasets to test with
        String[] datasets = { "Foo", "Bar" };
        for (String datasetId : datasets) {
            datasetRequest(HttpStatus.CREATED).putDataset(datasetId, null);
        }

        // Test list shows these datasets
        ResponseEntity<EntityListResponse> list = datasetRequest(HttpStatus.OK).listDatasets();
        List<EntityReference> items = list.getBody().getData();
        List<String> savedDatasetIds = items.stream().map(i -> i.getId()).collect(Collectors.toList());
        Assert.assertTrue(savedDatasetIds.containsAll(Arrays.asList(datasets)));
    }

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
        Map<String, MultiStatusResponse.Response> datasets = executeBatchCreateRequestTest(3);

        executeBatchUpdateRequestTest(datasets, (datasetId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch(datasets.get(datasetId).getEntityTag());
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestIfMatchWildSucceeds() {
        // Issue a batch POST datasetRequest to create initial data
        Map<String, MultiStatusResponse.Response> datasets = executeBatchCreateRequestTest(3);

        executeBatchUpdateRequestTest(datasets, (datasetId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch("*");
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestIfMatchFails() {
        // Issue a batch POST datasetRequest to create initial data
        Map<String, MultiStatusResponse.Response> datasets = executeBatchCreateRequestTest(3);

        // Now for each dataset, attempt an update with If-Match set to an invalid
        executeBatchUpdateRequestTest(datasets, (datasetId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return Pair.of(preconditions, HttpStatus.PRECONDITION_FAILED);
        });
    }

    @Test
    public void testPostRequestIfUnmodifiedSinceSucceeds() {
        // Issue a batch POST datasetRequest to create initial data
        Map<String, MultiStatusResponse.Response> datasets = executeBatchCreateRequestTest(3);

        executeBatchUpdateRequestTest(datasets, (datasetId) -> {
            Preconditions preconditions = new Preconditions();
            preconditions.setIfUnmodifiedSince(datasets.get(datasetId).getLastModified());
            return Pair.of(preconditions, HttpStatus.OK);
        });
    }

    @Test
    public void testPostRequestIfUnmodifiedSinceFails() {
        // Issue a batch POST datasetRequest to create initial data
        Map<String, MultiStatusResponse.Response> datasets = executeBatchCreateRequestTest(3);

        executeBatchUpdateRequestTest(datasets, (datasetId) -> {
            Date lastModified = datasets.get(datasetId).getLastModified();
            Date beforeLastModified = new Date(lastModified.getTime() - 1000);

            Preconditions preconditions = new Preconditions();
            preconditions.setIfUnmodifiedSince(beforeLastModified);
            return Pair.of(preconditions, HttpStatus.PRECONDITION_FAILED);
        });
    }


    private void executeConditionalGetRequestTest(HttpStatus expected,
            Function<ResponseEntity<DatasetEntityResponse>, HttpHeaders> getRequestHeaderProvider) {
        // First PUT a new dataset
        ResponseEntity<DatasetEntityResponse> createResponse = createTestDataset();
        Dataset original = createResponse.getBody().getData();

        // Now attempt to get the same dataset using a conditional datasetRequest
        HttpHeaders headers = getRequestHeaderProvider.apply(createResponse);
        ResponseEntity<DatasetEntityResponse> getResponse = datasetRequest(expected)
                .withHeaders(headers)
                .getDataset(original.getId());

        if (expected == HttpStatus.NOT_MODIFIED) {
            Assert.assertNull(getResponse.getBody());
        } else {
            Assert.assertNotNull(getResponse.getBody());
        }
    }

    private void executeConditionalPutRequestTest(HttpStatus expected,
            Function<ResponseEntity<DatasetEntityResponse>, HttpHeaders> updateRequestHeaderProvider) {
        // First PUT a new dataset
        ResponseEntity<DatasetEntityResponse> createResponse = createTestDataset();
        Dataset original = createResponse.getBody().getData();

        // Now attempt to update the same dataset using a conditional datasetRequest and add properties
        HttpHeaders headers = updateRequestHeaderProvider.apply(createResponse);

        // DatasetEntity properties to be added
        DatasetProperties props = new DatasetProperties();
        props.setOriginatorEmail("test@example.com");

        ResponseEntity<DatasetEntityResponse> updateResponse = datasetRequest(expected).withHeaders(headers).putDataset(original.getId(), props);

        if (updateResponse.getStatusCode().is2xxSuccessful()) {
            Dataset dataset = updateResponse.getBody().getData();
            Assert.assertEquals(original.getId(), dataset.getId());
            Assert.assertEquals(original.getCreated(), dataset.getCreated());
            Assert.assertNotEquals(original.getLastModified(), dataset.getLastModified());
            Assert.assertEquals(props.getOriginatorEmail(), dataset.getProperties().getOriginatorEmail());
        } else {
            Assert.assertNull(updateResponse.getBody().getData());
        }
    }

    private void executeConditionalDeleteRequestTest(HttpStatus expected,
            Function<ResponseEntity<DatasetEntityResponse>, HttpHeaders> deleteRequestHeaderProvider) {
        // First PUT a new dataset
        ResponseEntity<DatasetEntityResponse> createResponse = createTestDataset();
        Dataset original = createResponse.getBody().getData();

        // Now attempt to delete the same dataset using a conditional datasetRequest
        HttpHeaders headers = deleteRequestHeaderProvider.apply(createResponse);

        ResponseEntity<?> deleteResponse = datasetRequest(expected).withHeaders(headers).deleteDataset(original.getId());
        if (deleteResponse.getStatusCode().is2xxSuccessful()) {
            Assert.assertNull(deleteResponse.getBody());
            // Check that the record has actually been deleted
            datasetRequest(HttpStatus.NOT_FOUND).getDataset(original.getId());
        } else {
            Assert.assertNotNull(deleteResponse.getBody());
        }
    }

    private Map<String, MultiStatusResponse.Response> executeBatchCreateRequestTest(int count) {
        Map<String, MultiStatusResponse.Response> datasets = new LinkedHashMap<>();

        // Issue a batch POST datasetRequest and record the response
        List<BatchDatasetRequestItem> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BatchDatasetRequestItem request = new BatchDatasetRequestItem();
            String datasetId = "batch_test_dataset_" + i;
            request.setDatasetId(datasetId);
            requests.add(request);
        }
        ResponseEntity<MultiStatusResponse> createResponse = datasetRequest(HttpStatus.MULTI_STATUS)
                .postDatasets(new BatchDatasetRequest(requests));
        List<MultiStatusResponse.Response> responses = createResponse.getBody().getData();
        for (int i = 0; i < responses.size(); i++) {
            final MultiStatusResponse.Response response = responses.get(i);
            final String expectedDatasetId = "batch_test_dataset_" + i;

            Assert.assertEquals(HttpStatus.CREATED.value(), response.getCode());
            Assert.assertNotNull(response.getEntityTag());
            Assert.assertNotNull(response.getLastModified());
            Assert.assertNotNull(response.getHref());
            Assert.assertEquals(expectedDatasetId, response.getId());
            datasets.put(response.getId(), response);
        }
        return datasets;
    }

    private ResponseEntity<MultiStatusResponse> executeBatchUpdateRequestTest(Map<String, MultiStatusResponse.Response> originalDatasets,
            Function<String, Pair<Preconditions, HttpStatus>> preconditionProvider) {

        // Map of expected response codes for each id
        Map<String, HttpStatus> expectations = new HashMap<>();

        // Now for each dataset, attempt an update with If-Match set to the proper value
        List<BatchDatasetRequestItem> requests = new ArrayList<>();
        for (String id : originalDatasets.keySet()) {
            BatchDatasetRequestItem request = new BatchDatasetRequestItem();
            request.setDatasetId(id);

            Pair<Preconditions, HttpStatus> conditionPair = preconditionProvider.apply(id);
            request.setPreconditions(conditionPair.getLeft());
            expectations.put(id, conditionPair.getRight());

            DatasetProperties properties = new DatasetProperties();
            properties.setOriginatorEmail("test@example.com");
            request.setProperties(properties);
            requests.add(request);
        }

        ResponseEntity<MultiStatusResponse> responses = datasetRequest(HttpStatus.MULTI_STATUS).postDatasets(new BatchDatasetRequest(requests));
        responses.getBody().getData().forEach((response) -> {
            HttpStatus expected = expectations.get(response.getId());
            HttpStatus status = HttpStatus.valueOf(response.getCode());

            // Response at creation time
            MultiStatusResponse.Response createResponse = originalDatasets.get(response.getId());

            // Common assertions regardless of http status
            Assert.assertEquals(expected, status);
            Assert.assertEquals(createResponse.getId(), response.getId());
            Assert.assertEquals(createResponse.getHref(), response.getHref());

            if (status == HttpStatus.OK) {
                Assert.assertNotEquals(createResponse.getEntityTag(), response.getEntityTag());
                Assert.assertNotEquals(createResponse.getLastModified(), response.getLastModified());
            } else if (status != HttpStatus.PRECONDITION_FAILED) {
                Assert.fail("Unexpected response " + status.toString());
            }
        });
        return responses;
    }
}