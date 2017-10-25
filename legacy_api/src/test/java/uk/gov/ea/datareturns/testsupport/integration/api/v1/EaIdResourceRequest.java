package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.EaIdResource;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EaIdEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 18/05/17.
 */
public class EaIdResourceRequest extends AbstractResourceRequest {
    public EaIdResourceRequest(RestfulTest testClass, HttpStatus expected) {
        super(testClass, expected);
    }

    public ResponseEntity<EntityReferenceListResponse> listEaIds() {
        return get(uri(EaIdResource.class, "listEaIds"),
                null, EntityReferenceListResponse.class);
    }

    public ResponseEntity<EaIdEntityResponse> getEaId(String eaIdId) {
        return get(uri(EaIdResource.class, "getEaId", templateValues(eaIdId))
                , null, EaIdEntityResponse.class);
    }

    private Map<String, Object> templateValues(String eaIdId) {
        Map<String, Object> values = new HashMap<>();
        values.put("ea_id", eaIdId);
        return values;
    }

    @Override public EaIdResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }
}
