package uk.gov.ea.datareturns.tests.integration.resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class APIIntegrationTests_Dataset {

    @Inject private DatasetService datasetService;
    @Inject private SitePermitService sitePermitService;

    private static final String TEST_SITE_NAME = "TEST_SITE";
    private static final String UNIQUE_ID = "EA_ID1";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String DATASET_IDENTIFIER = "DS_01";

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException, SitePermitService.SitePermitServiceException {
        sitePermitService.removePermitSiteAndAliases(UNIQUE_ID);
        sitePermitService.addNewPermitAndSite(UNIQUE_ID, TEST_SITE_NAME);
    }

    @After public void down() throws IOException, SitePermitService.SitePermitServiceException {
        sitePermitService.removePermitSiteAndAliases(UNIQUE_ID);
    }

    @Test
    public void createSystemManagedDataset() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setIdentifier(DATASET_IDENTIFIER);
        datasetService.createDataset(UNIQUE_ID, dataset);

        Assert.assertEquals(UNIQUE_ID, dataset.getParentCollection().getUniqueIdentifier().getName());
    }

    @Test
    public void createUserManagedDatasetAutonamed() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        datasetService.createDataset(UNIQUE_ID, dataset);
        Assert.assertNotNull(dataset.getParentCollection().getUniqueIdentifier());
    }

    @Test
    public void testDatasetModificationSetsChangeDate() {

        //  Create new dataset
        DatasetEntity dataset = new DatasetEntity();
        dataset.setIdentifier(DATASET_IDENTIFIER);
        datasetService.createDataset(UNIQUE_ID, dataset);

        // Test the dataset creation date is set the EA_ID
        Instant createDate = dataset.getParentCollection().getLastChangedDate();
        Assert.assertNotNull(createDate);

        // Modify the dataset
        dataset.setOriginatorEmail("some.email@email.com");
        datasetService.updateDataset(dataset);

        // Test that the dataset modification date has been changed
        Instant changedDate = dataset.getParentCollection().getLastChangedDate();
        Assert.assertNotEquals(changedDate, createDate);

        UniqueIdentifier eaId = dataset.getParentCollection().getUniqueIdentifier();

        // Remove the dataset
        datasetService.removeDataset(UNIQUE_ID, DATASET_IDENTIFIER);
        DatasetCollection collection = datasetService.getDatasets(eaId);
        Instant deleteDate = collection.getLastChangedDate();
        Assert.assertNotNull(deleteDate);
        Assert.assertNotEquals(changedDate, deleteDate);
    }

}
