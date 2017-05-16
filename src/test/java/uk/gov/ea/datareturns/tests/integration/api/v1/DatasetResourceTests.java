package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.web.config.JerseyConfig;
import uk.gov.ea.datareturns.web.resource.v1.DatasetResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.Metadata;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ResponseWrapper;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetListResponse;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
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
public class DatasetResourceTests {
    @LocalServerPort
    private int port;
    @Inject
    private TestRestTemplate restTemplate;

    @Before
    public void beforeTest() {
        DatasetRequest.configure(restTemplate, port);
        // Ensure no datasets exist prior to each test
        ResponseEntity<DatasetListResponse> list = DatasetRequest.build(HttpStatus.OK).listDatasets();
        Assert.assertTrue(list.getBody().getData().getItems().isEmpty());
    }

    @After
    public void afterTest() {
        // Clear down all datasets after each test
        ResponseEntity<DatasetListResponse> list = DatasetRequest.build(HttpStatus.OK).listDatasets();
        for (EntityReference ref : list.getBody().getData().getItems()) {
            String dsId = ref.getId();
            DatasetRequest.build(HttpStatus.NO_CONTENT).deleteDataset(dsId);
        }
    }

    @Test
    public void testListDatasets() {
        // Add datasets to test with
        String[] datasets = { "Foo", "Bar" };
        for (String datasetId : datasets) {
            DatasetRequest.build(HttpStatus.CREATED).putDataset(datasetId, null);
        }

        // Test list shows these datasets
        ResponseEntity<DatasetListResponse> list = DatasetRequest.build(HttpStatus.OK).listDatasets();
        List<EntityReference> items = list.getBody().getData().getItems();
        List<String> savedDatasetIds = items.stream().map(i -> i.getId()).collect(Collectors.toList());
        Assert.assertTrue(savedDatasetIds.containsAll(Arrays.asList(datasets)));
    }

    @Test
    public void testConditionalGetRequestNotModified() {
        // First PUT a new dataset
        String datasetId = UUID.randomUUID().toString();

        ResponseEntity<DatasetEntityResponse> putResponse = DatasetRequest.build(HttpStatus.CREATED).putDataset(datasetId, null);

        // Issue GET for same resource with If-None-Match conditional request header
        HttpHeaders headers = new HttpHeaders();
        headers.setIfNoneMatch(putResponse.getHeaders().getETag());
        ResponseEntity<DatasetEntityResponse> getResponse = DatasetRequest.build(HttpStatus.NOT_MODIFIED)
                .withHeaders(headers)
                .getDataset(datasetId);
        Assert.assertNull(getResponse.getBody());
    }

    @Test
    public void testConditionalPutRequestSucceeds() {
        // First PUT a new dataset
        String datasetId = UUID.randomUUID().toString();
        ResponseEntity<DatasetEntityResponse> createResponse = DatasetRequest.build(HttpStatus.CREATED).putDataset(datasetId, null);

        Dataset original = createResponse.getBody().getData();
        Assert.assertNotNull(original.getCreated());
        Assert.assertNull(original.getProperties());

        // Now update the same dataset using a conditional request and add properties

        // Add headers with If-Match equal to the ETag given in the response from the create request
        HttpHeaders headers = new HttpHeaders();
        headers.setIfMatch(createResponse.getHeaders().getETag());

        // DatasetEntity properties to be added
        DatasetProperties props = new DatasetProperties();
        props.setOriginatorEmail("test@example.com");

        ResponseEntity<DatasetEntityResponse> updateResponse = DatasetRequest.build(HttpStatus.OK)
                .withHeaders(headers)
                .putDataset(datasetId, props);

        Dataset dataset = updateResponse.getBody().getData();
        Assert.assertEquals(original.getId(), dataset.getId());
        Assert.assertEquals(original.getCreated(), dataset.getCreated());
        Assert.assertEquals("test@example.com", dataset.getProperties().getOriginatorEmail());
    }

    @Test
    public void testConditionalPutRequestFails() {
        // First PUT a new dataset
        String datasetId = UUID.randomUUID().toString();
        ResponseEntity<DatasetEntityResponse> createResponse = DatasetRequest.build(HttpStatus.CREATED).putDataset(datasetId, null);

        Dataset original = createResponse.getBody().getData();
        Assert.assertNotNull(original.getCreated());
        Assert.assertNull(original.getProperties());

        // Now attempt to update the same dataset using a conditional request and add properties

        // Add headers with If-Match equal to the ETag given in the response from the create request
        HttpHeaders headers = new HttpHeaders();
        headers.setIfMatch("\"A_VALUE_THAT_SHOULD_NEVER_MATCH\"");

        // DatasetEntity properties to be added
        DatasetProperties props = new DatasetProperties();
        props.setOriginatorEmail("test@example.com");

        ResponseEntity<DatasetEntityResponse> updateResponse = DatasetRequest.build(HttpStatus.PRECONDITION_FAILED)
                .withHeaders(headers)
                .putDataset(datasetId, props);
        Assert.assertNull(updateResponse.getBody().getData());
    }

    public static class DatasetRequest {
        private static int port;
        private static TestRestTemplate restTemplate;

        private HttpStatus expected;
        private HttpHeaders headers;

        public ResponseEntity<DatasetListResponse> listDatasets() {
            URI uri = uri("listDatasets");
            ResponseEntity<DatasetListResponse> response = restTemplate.getForEntity(uri, DatasetListResponse.class);
            testResponse(HttpStatus.OK, response);
            return response;
        }

        public ResponseEntity<DatasetEntityResponse> getDataset(String datasetId) {
            URI uri = uri("getDataset", datasetId);
            ResponseEntity<DatasetEntityResponse> response = executeRequest(uri, HttpMethod.GET, null, DatasetEntityResponse.class);
            testResponse(expected, response);
            return response;
        }

        public ResponseEntity<DatasetEntityResponse> putDataset(String datasetId, DatasetProperties properties) {
            URI uri = uri("putDataset", datasetId);
            ResponseEntity<DatasetEntityResponse> response = executeRequest(uri, HttpMethod.PUT, properties, DatasetEntityResponse.class);
            testResponse(expected, response);
            if (expected.is2xxSuccessful()) {
                Assert.assertNotNull(response.getHeaders().getETag());
                Assert.assertNotNull(response.getBody().getData());
                Dataset data = response.getBody().getData();
                Assert.assertEquals(datasetId, data.getId());
            }
            return response;
        }

        public ResponseEntity<?> deleteDataset(String datasetId) {
            URI uri = uri("deleteDataset", datasetId);
            ResponseEntity<DatasetEntityResponse> response = executeRequest(uri, HttpMethod.DELETE, null, DatasetEntityResponse.class);
            testResponse(expected, response);
            return response;
        }

        private <T> ResponseEntity<T> executeRequest(URI uri, HttpMethod method, Object requestEntity, Class<T> responseType) {
            HttpEntity<?> entity = new HttpEntity<>(requestEntity, headers);
            return restTemplate.exchange(uri, method, entity, responseType);
        }

        private void testResponse(HttpStatus expected, ResponseEntity<? extends ResponseWrapper> response) {
            Assert.assertEquals(expected, response.getStatusCode());

            // Test metadata
            Set<HttpStatus> emptyResponseStatuses = new HashSet<>();
            emptyResponseStatuses.add(HttpStatus.NOT_MODIFIED);
            emptyResponseStatuses.add(HttpStatus.NO_CONTENT);
            //            emptyResponseStatuses.add(HttpStatus.PRECONDITION_FAILED);

            if (emptyResponseStatuses.contains(response.getStatusCode())) {
                Assert.assertNull(response.getBody());
            } else {
                // Test response body
                Metadata metadata = response.getBody().getMeta();
                Assert.assertEquals(response.getStatusCodeValue(), metadata.getStatus());

                if (expected.is2xxSuccessful()) {
                    Assert.assertNull(metadata.getErrorDescription());
                } else {
                    Assert.assertNotNull(metadata.getErrorDescription());
                }
            }
        }

        private URI uri(String method, String datasetId) {
            UriBuilder ub = UriBuilder.fromUri("http://localhost:{port}/{baseUri}");
            ub.resolveTemplate("port", port);
            ub.resolveTemplateFromEncoded("baseUri", JerseyConfig.APPLICATION_PATH.substring(1));

            ub.path(DatasetResource.class);

            if (method != null) {
                Optional<Method> classMethod = Arrays.stream(DatasetResource.class.getMethods())
                        .filter(m -> m.getName().equals(method) && m.isAnnotationPresent(Path.class))
                        .findFirst();
                if (classMethod.isPresent()) {
                    ub.path(classMethod.get());
                }
            }

            if (datasetId != null) {
                ub.resolveTemplate("dataset_id", datasetId);
            }

            return ub.build();
        }

        private URI uri(String method) {
            return uri(method, null);
        }

        public DatasetRequest withHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public static void configure(TestRestTemplate restTemplate, int serverPort) {
            DatasetRequest.restTemplate = restTemplate;
            DatasetRequest.port = serverPort;
        }

        public static DatasetRequest build(HttpStatus expected) {
            DatasetRequest request = new DatasetRequest();
            request.expected = expected;
            return request;
        }
    }

}
