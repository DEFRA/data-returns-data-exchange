package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.FieldRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.PayloadTypeRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.ValidationConstraintRepository;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.PayloadReference;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.ConstraintDefinition;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.FieldDefinition;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ConstraintDefinitionResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.FieldDefinitionResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.PayloadListResponse;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DefinitionResourceTests extends AbstractDataResourceTests {

    public static final String DATA_SAMPLE_PAYLOAD = "DataSamplePayload";
    @Inject
    PayloadTypeRepository payloadTypeRepository;

    @Inject
    FieldRepository fieldRepository;

    @Inject
    ValidationConstraintRepository validationConstraintRepository;

    /*
     * List payload types
     */
    @Test
    public void testListPayloadTypes() {
        Collection<String> expectedPayloads = Payload.NAMES.values();
        ResponseEntity<PayloadListResponse> responseEntity = definitionRequest(HttpStatus.OK).listPayloadTypes();
        Collection<PayloadReference> payloadReferences = responseEntity.getBody().getData();
        List<String> payloads = payloadReferences.stream().map(PayloadReference::getId).collect(Collectors.toList());
        Assert.assertTrue(payloads.containsAll(expectedPayloads));
    }

    /*
     * List fields
     */
    @Test
    public void listFields() {
        ResponseEntity<EntityReferenceListResponse> responseEntity = definitionRequest(HttpStatus.OK)
                .listFields(DATA_SAMPLE_PAYLOAD);

        List<EntityReference> fieldReferences = responseEntity.getBody().getData();
        List<String> fieldNames = fieldReferences.stream().map(EntityReference::getId).collect(Collectors.toList());

        Assert.assertTrue(getDatabaseFieldNames(DATA_SAMPLE_PAYLOAD).containsAll(fieldNames));
    }

    /*
     * Retrieve field definition
     */
    @Test
    public void retrieveFieldDefinitions() {

        // Not yet working in the client with controlled lists so need to fix that
        List<String> propertyNames = Arrays.asList(new String[] { "EA_ID", "Site_Name" });
        for (String fieldId : propertyNames) {

            ResponseEntity<FieldDefinitionResponse> responseEntity =
                    definitionRequest(HttpStatus.OK)
                            .getFieldDefinition(DATA_SAMPLE_PAYLOAD, fieldId);

            FieldDefinition fieldDefinition = responseEntity.getBody().getData();

            Assert.assertTrue(getDatabaseFieldNames(DATA_SAMPLE_PAYLOAD).contains(fieldDefinition.getId()));
        }
    }

    /*
     * List constraints
     */
    @Test
    public void listConstraints() {
        ResponseEntity<EntityReferenceListResponse> responseEntity = definitionRequest(HttpStatus.OK)
                .listValidationConstraints(DATA_SAMPLE_PAYLOAD);

        List<EntityReference> constraintDefinitions = responseEntity.getBody().getData();
        List<String> constraintDefinitionsNames = constraintDefinitions.stream().map(EntityReference::getId).collect(Collectors.toList());
        Assert.assertTrue(getDatabaseConstraintNames(DATA_SAMPLE_PAYLOAD).containsAll(constraintDefinitionsNames));
    }

    /*
     * Retrieve constraint definition
     */
    @Test
    public void retrieveConstraintDefinition() {
        for (String validationConstraintName : getDatabaseConstraintNames(DATA_SAMPLE_PAYLOAD)) {

            ResponseEntity<ConstraintDefinitionResponse> responseEntity = definitionRequest(HttpStatus.OK)
                    .getValidationConstraint(DATA_SAMPLE_PAYLOAD, validationConstraintName);

            ConstraintDefinition constraintDefinition = responseEntity.getBody().getData();
            Assert.assertEquals(constraintDefinition.getId(), validationConstraintName);
        }
    }

    private List<String> getDatabaseFieldNames(String payloadType) {
        PayloadType payloadTypeEntity = payloadTypeRepository.getOne(payloadType);
        List<FieldEntity> fields = fieldRepository.findAllByIdPayloadType(payloadTypeEntity);
        return fields.stream()
                .map(FieldEntity::getId)
                .map(FieldId::getFieldName)
                .collect(Collectors.toList());
    }

    private List<String> getDatabaseConstraintNames(String payloadType) {
        PayloadType payloadTypeEntity = payloadTypeRepository.getOne(payloadType);
        List<ValidationError> validationErrors = validationConstraintRepository.findAllByIdPayloadType(payloadTypeEntity);
        return validationErrors.stream()
                .map(ValidationError::getId)
                .map(ValidationErrorId::getError)
                .collect(Collectors.toList());
    }


}
