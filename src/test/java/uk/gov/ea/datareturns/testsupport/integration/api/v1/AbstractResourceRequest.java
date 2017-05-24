package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import uk.gov.ea.datareturns.web.config.JerseyConfig;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ResponseMetadata;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ResponseWrapper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by sam on 18/05/17.
 */
public abstract class AbstractResourceRequest {

    private static final Pattern MULTI_RESPONSE_STATUS_PATTERN = Pattern.compile("^HTTP/1.1\\s\\d{3}\\s(.*)");

    private final int port;
    private final TestRestTemplate template;

    private final HttpStatus expected;
    private HttpHeaders headers;

    public AbstractResourceRequest(RestfulTest testClassInstance, HttpStatus expected) {
        this.expected = expected;
        this.template = testClassInstance.getTemplate();
        this.port = testClassInstance.getPort();
    }

    protected <T extends ResponseWrapper> ResponseEntity<T> get(URI uri, Object requestEntity, Class<T> responseType) {
        return executeRequest(uri, HttpMethod.GET, requestEntity, responseType);
    }

    protected <T extends ResponseWrapper> ResponseEntity<T> put(URI uri, Object requestEntity, Class<T> responseType) {
        ResponseEntity<T> response = executeRequest(uri, HttpMethod.PUT, requestEntity, responseType);

        if (getExpected().is2xxSuccessful()) {
            Assert.assertNotNull(response.getHeaders().getETag());
            Assert.assertNotNull(response.getBody().getData());
        }
        return response;
    }

    protected <T extends ResponseWrapper> ResponseEntity<T> post(URI uri, Object requestEntity, Class<T> responseType) {
        ResponseEntity<T> response = executeRequest(uri, HttpMethod.POST, requestEntity, responseType);
        if (getExpected().is2xxSuccessful()) {
            Assert.assertNotNull(response.getBody());
        }
        return response;
    }

    public ResponseEntity<MultiStatusResponse> postBatchRequest(URI uri, Object requestEntity) {
        ResponseEntity<MultiStatusResponse> multiResponse = post(uri, requestEntity, MultiStatusResponse.class);

        for (MultiStatusResponse.Response response : multiResponse.getBody().getData()) {
            Assert.assertTrue(MULTI_RESPONSE_STATUS_PATTERN.matcher(response.getStatus()).matches());

            Assert.assertNotNull(response.getId());

            HttpStatus status = HttpStatus.valueOf(response.getCode());
            if (status.is2xxSuccessful()) {
                Assert.assertNotNull(response.getEntityTag());
                Assert.assertNotNull(response.getLastModified());
                Assert.assertNotNull(response.getHref());
            } else {
                Assert.assertNull(response.getEntityTag());
                Assert.assertNull(response.getLastModified());
            }
        }
        return multiResponse;
    }

    protected ResponseEntity<?> delete(URI uri) {
        // Will only ever have response body if it is an error response so use that.
        return executeRequest(uri, HttpMethod.DELETE, null, ErrorResponse.class);
    }

    protected <T extends ResponseWrapper> ResponseEntity<T> executeRequest(URI uri, HttpMethod method, Object requestEntity,
            Class<T> responseType) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            requestHeaders.putAll(headers);
        }
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(requestEntity, requestHeaders);
        ResponseEntity<T> responseEntity = template.exchange(uri, method, entity, responseType);
        testResponse(expected, responseEntity);
        return responseEntity;
    }

    private void testResponse(HttpStatus expected, ResponseEntity<? extends ResponseWrapper> response) {
        Assert.assertEquals(expected, response.getStatusCode());

        // Test metadata
        Set<HttpStatus> emptyResponseStatuses = new HashSet<>();
        emptyResponseStatuses.add(HttpStatus.NOT_MODIFIED);
        emptyResponseStatuses.add(HttpStatus.NO_CONTENT);

        if (emptyResponseStatuses.contains(response.getStatusCode())) {
            Assert.assertNull(response.getBody());
        } else {
            // Test response body
            Assert.assertNotNull(response.getBody());
            ResponseMetadata metadata = response.getBody().getMeta();
            Assert.assertEquals(response.getStatusCodeValue(), metadata.getStatus());

            if (expected.is2xxSuccessful()) {
                Assert.assertNull(metadata.getError());
            } else {
                Assert.assertNotNull(metadata.getError());
            }
        }
    }

    protected URI uri(Class resource, String methodName, Map<String, Object> templateValues) {
        UriBuilder ub = UriBuilder.fromUri("http://localhost:{port}/{baseUri}");
        ub.resolveTemplate("port", port);
        ub.resolveTemplateFromEncoded("baseUri", JerseyConfig.APPLICATION_PATH.substring(1));

        ub.path(resource);

        if (methodName != null) {
            Arrays.stream(resource.getMethods())
                    .filter(m -> m.getName().equals(methodName) && m.isAnnotationPresent(Path.class))
                    .findFirst()
                    .ifPresent((m) -> ub.path(m));
        }

        if (templateValues != null) {
            ub.resolveTemplates(templateValues);
        }

        return ub.build();
    }

    protected URI uri(Class resource, String methodName) {
        return this.uri(resource, methodName, null);
    }

    public abstract AbstractResourceRequest withHeaders(HttpHeaders headers);

    public int getPort() {
        return port;
    }

    public TestRestTemplate getTemplate() {
        return template;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public HttpStatus getExpected() {
        return expected;
    }

    public interface RestfulTest {
        TestRestTemplate getTemplate();

        int getPort();
    }
}
