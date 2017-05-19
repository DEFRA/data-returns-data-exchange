package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import uk.gov.ea.datareturns.web.config.JerseyConfig;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ErrorResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.Metadata;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ResponseWrapper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sam on 18/05/17.
 */
public abstract class AbstractResourceRequest {

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

    protected ResponseEntity<?> delete(URI uri) {
        // Will only ever have response body if it is an error response so use that.
        return executeRequest(uri, HttpMethod.DELETE, null, ErrorResponse.class);
    }

    protected <T extends ResponseWrapper> ResponseEntity<T> executeRequest(URI uri, HttpMethod method, Object requestEntity,
            Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(requestEntity, headers);
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
            Metadata metadata = response.getBody().getMeta();
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
        return uri(resource, methodName, null);
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
