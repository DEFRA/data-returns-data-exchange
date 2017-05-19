package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.testsupport.integration.api.v1.AbstractResourceRequest;
import uk.gov.ea.datareturns.testsupport.integration.api.v1.DatasetResourceRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.multistatus.MultiStatusResponse;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DatasetEntity resource tests
 *
 * @author Sam Gardner-Dell
 */

// TODO: Tidy up test framework and expand tests.
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DatasetResourceTests implements AbstractResourceRequest.RestfulTest {
    @LocalServerPort
    private int port;
    @Inject
    private TestRestTemplate template;

    @Override public int getPort() {
        return port;
    }

    @Override public TestRestTemplate getTemplate() {
        return template;
    }

    private DatasetResourceRequest request(HttpStatus expected) {
        return new DatasetResourceRequest(this, expected);
    }

    @Before
    public void beforeTest() {
        // Ensure no datasets exist prior to each test

        // Clear down all datasets after each test
        ResponseEntity<EntityListResponse> list = request(HttpStatus.OK).listDatasets();
        for (EntityReference ref : list.getBody().getData()) {
            String dsId = ref.getId();
            request(HttpStatus.NO_CONTENT).deleteDataset(dsId);
        }

        ResponseEntity<EntityListResponse> updatedList = request(HttpStatus.OK).listDatasets();
        Assert.assertTrue("Expected dataset list to be empty before each test.", updatedList.getBody().getData().isEmpty());
    }

    @Test
    public void testListDatasets() {
        // Add datasets to test with
        String[] datasets = { "Foo", "Bar" };
        for (String datasetId : datasets) {
            request(HttpStatus.CREATED).putDataset(datasetId, null);
        }

        // Test list shows these datasets
        ResponseEntity<EntityListResponse> list = request(HttpStatus.OK).listDatasets();
        List<EntityReference> items = list.getBody().getData();
        List<String> savedDatasetIds = items.stream().map(i -> i.getId()).collect(Collectors.toList());
        Assert.assertTrue(savedDatasetIds.containsAll(Arrays.asList(datasets)));
    }

    @Test
    public void testGetRequestIfNoneMatchPasses() {
        executeConditionalGetRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfNoneMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testGetRequestIfNoneMatchFails() {
        executeConditionalGetRequestTest(HttpStatus.NOT_MODIFIED, (createResponse) -> {
            // Set the update request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfNoneMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testGetRequestIfModifiedSincePasses() {
        executeConditionalGetRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfModifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    @Test
    public void testGetRequestIfModifiedSinceFails() {
        executeConditionalGetRequestTest(HttpStatus.NOT_MODIFIED, (createResponse) -> {
            // Set the update request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfModifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfMatchSucceeds() {
        executeConditionalPutRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update request headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfMatchFails() {
        executeConditionalPutRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the update request header to use a rubbish etag
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testPutRequestIfUnmodifiedSinceSucceeds() {
        executeConditionalPutRequestTest(HttpStatus.OK, (createResponse) -> {
            // Set the update request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testPutRequestIfUnmodifiedSinceFails() {
        executeConditionalPutRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the update request headers to be before the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfMatchSucceeds() {
        executeConditionalDeleteRequestTest(HttpStatus.NO_CONTENT, (createResponse) -> {
            // Set the delete request headers to match etag given from the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch(createResponse.getHeaders().getETag());
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfMatchFails() {
        executeConditionalDeleteRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the delete request header to use a rubbish etag
            HttpHeaders headers = new HttpHeaders();
            headers.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfUnmodifiedSinceSucceeds() {
        executeConditionalDeleteRequestTest(HttpStatus.NO_CONTENT, (createResponse) -> {
            // Set the delete request headers to match the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified());
            return headers;
        });
    }

    @Test
    public void testDeleteRequestIfUnmodifiedSinceFails() {
        executeConditionalDeleteRequestTest(HttpStatus.PRECONDITION_FAILED, (createResponse) -> {
            // Set the delete request headers to be before the last modification date given by the create response
            HttpHeaders headers = new HttpHeaders();
            headers.setIfUnmodifiedSince(createResponse.getHeaders().getLastModified() - 1000);
            return headers;
        });
    }

    private ResponseEntity<DatasetEntityResponse> createTestDataset() {
        String datasetId = UUID.randomUUID().toString();
        ResponseEntity<DatasetEntityResponse> createResponse = request(HttpStatus.CREATED).putDataset(datasetId, null);

        Dataset dataset = createResponse.getBody().getData();
        Assert.assertNotNull(dataset.getCreated());
        Assert.assertNotNull(dataset.getLastModified());
        Assert.assertEquals(dataset.getCreated(), dataset.getLastModified());
        Assert.assertNull(dataset.getProperties());
        return createResponse;
    }

    private void executeConditionalGetRequestTest(HttpStatus expected,
            Function<ResponseEntity<DatasetEntityResponse>, HttpHeaders> getRequestHeaderProvider) {
        // First PUT a new dataset
        ResponseEntity<DatasetEntityResponse> createResponse = createTestDataset();
        Dataset original = createResponse.getBody().getData();

        // Now attempt to get the same dataset using a conditional request
        HttpHeaders headers = getRequestHeaderProvider.apply(createResponse);
        ResponseEntity<DatasetEntityResponse> getResponse = request(expected)
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

        // Now attempt to update the same dataset using a conditional request and add properties
        HttpHeaders headers = updateRequestHeaderProvider.apply(createResponse);

        // DatasetEntity properties to be added
        DatasetProperties props = new DatasetProperties();
        props.setOriginatorEmail("test@example.com");

        ResponseEntity<DatasetEntityResponse> updateResponse = request(expected).withHeaders(headers).putDataset(original.getId(), props);

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

        // Now attempt to delete the same dataset using a conditional request
        HttpHeaders headers = deleteRequestHeaderProvider.apply(createResponse);

        ResponseEntity<?> deleteResponse = request(expected).withHeaders(headers).deleteDataset(original.getId());
        if (deleteResponse.getStatusCode().is2xxSuccessful()) {
            Assert.assertNull(deleteResponse.getBody());
        } else {
            Assert.assertNotNull(deleteResponse.getBody());
        }
    }

    @Test
    public void testConditionalPostRequestSucceeds() {
        // Issue a batch POST request to create initial data
        Map<String, MultiStatusResponse.Response> datasets = issueBatchCreateRequest();

        // Now for each dataset, attempt an update with If-Match set to entity tag returned be the previous response
        ResponseEntity<MultiStatusResponse> updateResponse = issueBatchUpdateRequest(datasets, MultiStatusResponse.Response::getEntityTag);

        // Test the responses and store each one in the datasets map
        testMultiResponse(updateResponse.getBody().getData(), HttpStatus.OK, response -> {
            MultiStatusResponse.Response lastResponse = datasets.get(response.getId());
            Assert.assertEquals(lastResponse.getHref(), response.getHref());
            Assert.assertNotNull(response.getEntityTag());
            Assert.assertNotNull(response.getLastModified());
            Assert.assertNotEquals(lastResponse.getEntityTag(), response.getEntityTag());
            Assert.assertNotEquals(lastResponse.getLastModified(), response.getLastModified());
        });
    }

    @Test
    public void testConditionalPostRequestFails() {
        // Issue a batch POST request to create initial data
        Map<String, MultiStatusResponse.Response> datasets = issueBatchCreateRequest();

        // Now for each dataset, attempt an update with If-Match set to an invalid
        ResponseEntity<MultiStatusResponse> updateResponse = issueBatchUpdateRequest(datasets, (p) ->
                "\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");
        // Test the responses and store each one in the datasets map
        testMultiResponse(updateResponse.getBody().getData(), HttpStatus.PRECONDITION_FAILED, response -> {
            MultiStatusResponse.Response lastResponse = datasets.get(response.getId());
            Assert.assertEquals(lastResponse.getHref(), response.getHref());
        });
    }

    private Map<String, MultiStatusResponse.Response> issueBatchCreateRequest() {
        Map<String, MultiStatusResponse.Response> datasets = new LinkedHashMap<>();
        datasets.put("dataset1", null);
        datasets.put("dataset2", null);
        datasets.put("dataset3", null);
        datasets.put("dataset4", null);
        datasets.put("dataset5", null);

        // Issue a batch POST request and record the responses
        List<BatchDatasetRequestItem> requests = new ArrayList<>();
        for (String dsid : datasets.keySet()) {
            BatchDatasetRequestItem request = new BatchDatasetRequestItem();
            request.setDatasetId(dsid);
            requests.add(request);
        }
        ResponseEntity<MultiStatusResponse> createResponse = request(HttpStatus.MULTI_STATUS)
                .postDatasets(new BatchDatasetRequest(requests));
        // Test the responses and store each one in the datasets map
        testMultiResponse(createResponse.getBody().getData(), HttpStatus.CREATED, response -> {
            Assert.assertNotNull(response.getEntityTag());
            Assert.assertNotNull(response.getLastModified());
            Assert.assertNotNull(response.getHref());
            datasets.put(response.getId(), response);
        });

        return datasets;
    }

    private ResponseEntity<MultiStatusResponse> issueBatchUpdateRequest(Map<String, MultiStatusResponse.Response> datasets,
            Function<MultiStatusResponse.Response, String>
                    etagProvider) {
        // Now for each dataset, attempt an update with If-Match set to the proper value
        List<BatchDatasetRequestItem> requests = new ArrayList<>();
        for (Map.Entry<String, MultiStatusResponse.Response> dataset : datasets.entrySet()) {
            BatchDatasetRequestItem request = new BatchDatasetRequestItem();
            request.setDatasetId(dataset.getKey());

            Preconditions preconditions = new Preconditions();
            preconditions.setIfMatch(etagProvider.apply(dataset.getValue()));
            request.setPreconditions(preconditions);

            DatasetProperties properties = new DatasetProperties();
            properties.setOriginatorEmail("test@example.com");
            request.setProperties(properties);

            requests.add(request);
        }

        return request(HttpStatus.MULTI_STATUS).postDatasets(new BatchDatasetRequest(requests));
    }

    private static void testMultiResponse(List<MultiStatusResponse.Response> responses, HttpStatus expected, Consumer<MultiStatusResponse
            .Response> action) {
        responses.forEach(r -> {
            Assert.assertEquals(expected.value(), r.getCode());
            Assert.assertNotNull(r.getId());
            Assert.assertTrue(r.getStatus().contains("HTTP/1.1 " + expected.value()));

            // Delegate to the consumer
            action.accept(r);
        });
    }

}