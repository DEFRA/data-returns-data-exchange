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
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DatasetResource tests
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class EaIdResourceTests extends AbstractDataResourceTests {

    @Test
    public void testListEaIds() {
        // Test list shows these eaIds
        ResponseEntity<EntityReferenceListResponse> list = eaIdRequest(HttpStatus.OK).listEaIds();
        List<EntityReference> items = list.getBody().getData();
        List<String> eaIds = items.stream().map(i -> i.getId()).collect(Collectors.toList());
        Assert.assertTrue(eaIds.contains(TEST_EA_ID));
    }
}