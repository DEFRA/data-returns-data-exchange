package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ea.datareturns.testsupport.integration.api.v1.AbstractResourceRequest;
import uk.gov.ea.datareturns.testsupport.integration.api.v1.DatasetResourceRequest;
import uk.gov.ea.datareturns.testsupport.integration.api.v1.RecordResourceRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;

import javax.inject.Inject;
import java.util.UUID;

/**
 * DatasetResource tests
 *
 * @author Sam Gardner-Dell
 */
public abstract class AbstractDataResourceTests implements AbstractResourceRequest.RestfulTest {
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

    protected DatasetResourceRequest datasetRequest(HttpStatus expected) {
        return new DatasetResourceRequest(this, expected);
    }

    protected RecordResourceRequest recordRequest(HttpStatus expected) {
        return new RecordResourceRequest(this, expected);
    }

    @Before
    public void beforeTest() {
        // Clear down all datasets before each test
        ResponseEntity<EntityReferenceListResponse> list = datasetRequest(HttpStatus.OK).listDatasets();
        for (EntityReference ref : list.getBody().getData()) {
            String dsId = ref.getId();
            datasetRequest(HttpStatus.NO_CONTENT).deleteDataset(dsId);
        }

        ResponseEntity<EntityReferenceListResponse> updatedList = datasetRequest(HttpStatus.OK).listDatasets();
        Assert.assertTrue("Expected dataset list to be empty before each test.", updatedList.getBody().getData().isEmpty());
    }

    protected ResponseEntity<DatasetEntityResponse> createTestDataset() {
        String datasetId = UUID.randomUUID().toString();
        ResponseEntity<DatasetEntityResponse> createResponse = datasetRequest(HttpStatus.CREATED).putDataset(datasetId, null);

        Dataset dataset = createResponse.getBody().getData();
        Assert.assertNotNull(dataset.getId());
        Assert.assertNotNull(dataset.getCreated());
        Assert.assertNotNull(dataset.getLastModified());
        Assert.assertEquals(dataset.getCreated(), dataset.getLastModified());
        Assert.assertNull(dataset.getProperties());
        return createResponse;
    }

}
