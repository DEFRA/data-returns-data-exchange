package uk.gov.ea.datareturns.tests.integration.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class APIIntegrationTests_Dataset {

    @Inject private DatasetService datasetService;

    private static final String USER_NAME = "Graham Willis";
    private static final String USER_NAME2 = "Graham Willis2";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String DATASET_ID = "DatasetEntity name";

    private static User user;
    private static DatasetEntity dataset;

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {
        user = datasetService.getUser(USER_NAME);
        if (user != null) {
            datasetService.getDatasets(user).forEach(ds -> datasetService.removeDataset(ds.getIdentifier()));
            datasetService.removeUser(USER_NAME);
        }

        User user2 = datasetService.getUser(USER_NAME2);
        if (user2 != null) {
            datasetService.getDatasets(user2).forEach(ds -> datasetService.removeDataset(ds.getIdentifier()));
            datasetService.removeUser(USER_NAME2);
        }

        user = datasetService.createUser(USER_NAME);
        dataset = new DatasetEntity();
        dataset.setUser(user);
        datasetService.createDataset(dataset);
    }

    @Test
    public void createUser() {
        Assert.assertEquals(user.getIdentifier(), USER_NAME);
    }

    @Test
    public void getSystemUser() {
        User systemUser = datasetService.getSystemUser();
        Assert.assertEquals(systemUser.getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createSystemManagedDataset() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        datasetService.createDataset(dataset);
        Assert.assertEquals(dataset.getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUserManagedDatasetAutonamed() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setUser(user);
        datasetService.createDataset(dataset);

        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
        List<DatasetEntity> datasets = datasetService.getDatasets(user);
        Assert.assertEquals(2, datasets.size());
    }

    @Test
    public void createUserManagedDatasetNamed() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setUser(user);
        dataset.setIdentifier(DATASET_ID);
        datasetService.createDataset(dataset);

        List<DatasetEntity> datasets = datasetService.getDatasets(user);
        Assert.assertEquals(2, datasets.size());

        DatasetEntity ds = datasetService.getDataset(DATASET_ID, user);

        Assert.assertEquals(ds.getUser().getIdentifier(), USER_NAME);
        Assert.assertEquals(ds.getIdentifier(), DATASET_ID);

        datasetService.removeDataset(DATASET_ID, user);
        datasets = datasetService.getDatasets(user);
        Assert.assertEquals(1, datasets.size());
    }

    @Test
    public void testThatDatasetAdditionDeletionAndModificationUpdateTheUserDatasetChangeDate() {
        // Create new user
        User user = datasetService.createUser(USER_NAME2);

        //  Create new dataset
        DatasetEntity dataset = new DatasetEntity();
        dataset.setUser(user);
        dataset.setIdentifier(DATASET_ID);
        datasetService.createDataset(dataset);

        // Test the dataset creation date is set on the user
        user = datasetService.getUser(user.getIdentifier());
        Instant createDate = user.getDatasetChangedDate();
        Assert.assertNotNull(createDate);

        // Modify the dataset
        dataset.setOriginatorEmail("Some email");
        datasetService.updateDataset(dataset);

        // Test that the dataset modification date has been set
        user = datasetService.getUser(user.getIdentifier());
        Instant changeDate = user.getDatasetChangedDate();
        Assert.assertNotNull(changeDate);
        Assert.assertNotEquals(changeDate, createDate);

        // Remove the dataset
        datasetService.removeDataset(DATASET_ID, user);
        user = datasetService.getUser(user.getIdentifier());
        Instant deleteDate = user.getDatasetChangedDate();
        Assert.assertNotNull(deleteDate);
        Assert.assertNotEquals(changeDate, deleteDate);
    }

}
