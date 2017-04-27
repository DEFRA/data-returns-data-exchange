package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;

import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Test to run against the service layer for submission (user) data
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SubmissionServiceTests {

    private final String USER_NAME = "Test User";
    private final String DATASET_ID = "AUG2018Q2";
    private final String RECORD_ID = "UM1009001";
    private final String[] RECORDS = { "AA0001", "AA002", "AA003" };

    @Inject
    SubmissionService submissionService;

    @Before
    public void init() {
        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }
    }

    @Test
    public void getSystemUser() {
        User systemUser = submissionService.getSystemUser();
        Assert.assertEquals(systemUser.getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUser() {
        User newUser = submissionService.createUser(USER_NAME);
        Assert.assertEquals(newUser.getIdentifier(), USER_NAME);
    }

    @Test
    public void createSystemManagedDataset() {
        Dataset dataset = submissionService.createDataset();
        Assert.assertEquals(dataset.getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUserManagedDatasetAutonamed() {
        User newUser = submissionService.createUser(USER_NAME);
        Dataset dataset = submissionService.createDataset(newUser);
        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
    }

    @Test
    public void createUserManagedDatasetNamed() {
        User newUser = submissionService.createUser(USER_NAME);
        Dataset dataset = submissionService.createDataset(newUser, DATASET_ID);
        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
        Assert.assertEquals(dataset.getIdentifier(), DATASET_ID);
    }

    @Test
    public void createSystemManagedRecordAndDataset() {
        Record record = submissionService.createRecord();
        Assert.assertEquals(record.getDataset().getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createSystemManagedRecord() {
        Dataset dataset = submissionService.createDataset();
        Record record = submissionService.createRecord(dataset);
        Assert.assertEquals(record.getDataset().getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUserManagedRecord() {
        User newUser = submissionService.createUser(USER_NAME);
        Dataset dataset = submissionService.createDataset(newUser, DATASET_ID);
        Record record = submissionService.createRecord(dataset, RECORD_ID);
        Assert.assertEquals(record.getIdentifier(), RECORD_ID);
    }

    @Test
    public void createUserManagedRecords() {
        User newUser = submissionService.createUser(USER_NAME);
        Dataset dataset = submissionService.createDataset(newUser, DATASET_ID);
        List<Record> records = submissionService.createRecords(dataset, Arrays.asList(RECORDS));
        Assert.assertEquals(records.size(), 3);
    }

    @Test
    public void getRecord() {
        User newUser = submissionService.createUser(USER_NAME);
        Dataset dataset = submissionService.createDataset(newUser, DATASET_ID);
        List<Record> records = submissionService.createRecords(dataset, Arrays.asList(RECORDS));
        Record record = submissionService.getRecord(RECORDS[1]);
        Assert.assertEquals(record.getIdentifier(), RECORDS[1]);
    }


}
