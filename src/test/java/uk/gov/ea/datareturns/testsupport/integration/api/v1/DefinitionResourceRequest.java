package uk.gov.ea.datareturns.testsupport.integration.api.v1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.web.resource.v1.DefinitionsResource;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ConstraintDefinitionResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.FieldDefinitionResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.PayloadListResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Graham Willis
 */
public class DefinitionResourceRequest extends AbstractResourceRequest {

    public DefinitionResourceRequest(RestfulTest testClassInstance, HttpStatus expected) {
        super(testClassInstance, expected);
    }

    @Override
    public AbstractResourceRequest withHeaders(HttpHeaders headers) {
        setHeaders(headers);
        return this;
    }

    public ResponseEntity<PayloadListResponse> listPayloadTypes() {
        URI uri = uri(DefinitionsResource.class, "listPayloads");
        return get(uri, null, PayloadListResponse.class);
    }

    public ResponseEntity<EntityListResponse> listFields(String payloadType) {
        URI uri = uri(DefinitionsResource.class, "listFields", templateValuesPayload(payloadType));
        return get(uri, null, EntityListResponse.class);
    }

    public ResponseEntity<FieldDefinitionResponse> getFieldDefinition(String payloadType, String propertyName) {
        URI uri = uri(DefinitionsResource.class, "getFieldDefinition", templateValuesField(payloadType, propertyName));
        return get(uri, null, FieldDefinitionResponse.class);
    }

    public ResponseEntity<EntityListResponse> listValidationConstraints(String payloadType) {
        URI uri = uri(DefinitionsResource.class, "listValidationConstraints", templateValuesPayload(payloadType));
        return get(uri, null, EntityListResponse.class);
    }

    public ResponseEntity<ConstraintDefinitionResponse> getValidationConstraint(String payloadType,
                                                                                String validationConstraint) {

        URI uri = uri(DefinitionsResource.class, "getValidationConstraint",
                templateValuesConstraint(payloadType, validationConstraint));

        return get(uri, null, ConstraintDefinitionResponse.class);
    }

    private Map<String,Object> templateValuesConstraint(String payloadType, String validationConstraint) {
        Map<String, Object> values = new HashMap<>();
        values.put("payload_type", payloadType);
        values.put("constraint_id", validationConstraint);
        return values;
    }

    private Map<String, Object> templateValuesPayload(String payloadType) {
        Map<String, Object> values = new HashMap<>();
        values.put("payload_type", payloadType);
        return values;
    }

    private Map<String,Object> templateValuesField(String payloadType, String fieldId) {
        Map<String, Object> values = new HashMap<>();
        values.put("payload_type", payloadType);
        values.put("field_id", fieldId);
        return values;
    }
}
