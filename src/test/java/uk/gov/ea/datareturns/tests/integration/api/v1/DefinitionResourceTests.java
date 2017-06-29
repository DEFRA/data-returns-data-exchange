package uk.gov.ea.datareturns.tests.integration.api.v1;

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
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.PayloadReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.PayloadListResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DefinitionResourceTests extends AbstractDataResourceTests {

    /*
     * List payload types
     */
    @Test
    public void testListPayloadTypes() {
        String[] expectedPayloads = { "DataSamplePayload", "DemonstrationAlternativePayload" };

        ResponseEntity<PayloadListResponse> responseEntity = definitionRequest(HttpStatus.OK).listPayloadTypes();
        Collection<PayloadReference> payloadReferences = responseEntity.getBody().getData();
        List<String> payloads = payloadReferences.stream().map(PayloadReference::getId).collect(Collectors.toList());
        Assert.assertTrue(payloads.containsAll(Arrays.asList(expectedPayloads)));
    }

    /*
     * List fields
     */
    @Test
    public void listFields() {
        ResponseEntity<EntityListResponse> responseEntity = definitionRequest(HttpStatus.OK)
                .listFields("DataSamplePayload");

        List<EntityReference> fieldReferences = responseEntity.getBody().getData(); 
                
    }

    /*
     * Retrieve field definition
     */
    @Test
    public void retrieveFieldDefinition() {}

    /*
     * List constraints
     */
    @Test
    public void listConstraints() {}

    /*
     *Retrieve constraint definition
     */
    @Test
    public void retrieveConstraintDefinition() {}

}
