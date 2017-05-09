package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.impl.BasicMeasurementMvo;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class APIIntegrationTests_BasicMeasurement {
    @Inject
    SubmissionService<BasicMeasurementDto, BasicMeasurement, BasicMeasurementMvo> submissionService;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_SUCCESS = "json/measurements-success.json";
    private final static String SUBMISSION_FAILURE = "json/measurements-fail.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String DATASET_ID = "Dataset name";
    private static final String[] RECORDS = { "AA0001", "AA002", "AA003" };

    private static User user;
    private static Dataset dataset;

    private static List<BasicMeasurementDto> samples;

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {
        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }
        user = submissionService.createUser(USER_NAME);
        dataset = submissionService.createDataset(user);
        samples = submissionService.parse(readTestFile(SUBMISSION_SUCCESS));
    }

    @Test
    public void createUser() {
        Assert.assertEquals(user.getIdentifier(), USER_NAME);
    }

    @Test
    public void getSystemUser() {
        User systemUser = submissionService.getSystemUser();
        Assert.assertEquals(systemUser.getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createSystemManagedDataset() {
        Dataset dataset = submissionService.createDataset();
        Assert.assertEquals(dataset.getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUserManagedDatasetAutonamed() {
        Dataset dataset = submissionService.createDataset(user);
        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
        List<Dataset> datasets = submissionService.getDatasets(user);
        Assert.assertEquals(2, datasets.size());
        submissionService.removeDataset(datasets.get(1).getIdentifier());
        datasets = submissionService.getDatasets(user);
        Assert.assertEquals(1, datasets.size());
    }

    @Test
    public void createUserManagedDatasetNamed() {
        Dataset dataset = submissionService.createDataset(user, DATASET_ID);
        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
        Assert.assertEquals(dataset.getIdentifier(), DATASET_ID);
    }

    @Test public void createTestRemoveRecords() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DatumIdentifierPair<>(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        Assert.assertEquals(RECORDS.length, records.size());
        for (String id : RECORDS) {
            Assert.assertTrue(submissionService.recordExists(dataset, id));
            submissionService.removeRecord(dataset, id);
            Assert.assertFalse(submissionService.recordExists(dataset, id));
        }
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewUserRecords() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DatumIdentifierPair(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewSystemRecords() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DatumIdentifierPair());
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a system identifier. The records
    // should all have a status of PARSED
    @Test public void createNewSystemRecordsWithSample() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a user identifier. The records
    // should all have a status of PARSED
    @Test public void createNewUserRecordsWithSample() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        int i = 0;
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(Integer.valueOf(i++).toString(), sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of records and then associate data samples with them
    // as a secondary step
    @Test public void createNewUserRecordsAndAddSample() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DatumIdentifierPair(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));

        list = new ArrayList<>();
        for (int i = 0; i < Math.min(RECORDS.length, samples.size()); i++) {
            list.add(new SubmissionService.DatumIdentifierPair(RECORDS[i], samples.get(i)));
        }
        records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create and validate a set of valid records
    @Test public void createAndValidateValidRecords() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a valid set of records and submit them
    @Test public void createValidateAndSubmitRecords() {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.VALID, r.getRecordStatus()));
        submissionService.submit(records);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.SUBMITTED, r.getRecordStatus()));
    }

    // Create and validate a set of valid and invalid records
    @Test public void createAndValidateValidAndInvalidRecords() throws IOException {
        List<BasicMeasurementDto> samples = submissionService.parse(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        Assert.assertEquals(5, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.VALID).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid and invalid records and submit them
    @Test public void createAndValidateValidAndInvalidAndSubmitRecords() throws IOException {
        List<BasicMeasurementDto> samples = submissionService.parse(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        Assert.assertEquals(5, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.SUBMITTED).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid records, submit and retrieve them by dataset
    @Test public void createAndValidateAndSubmitAndRetrieveRecords() throws IOException {
        List<SubmissionService.DatumIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DatumIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        List<Record> recs = submissionService.retrieve(dataset);
        recs.stream().map(r -> r.getAbstractMeasurement()).map(m -> m.getRecord()).forEach(
                r -> Assert.assertEquals(Record.RecordStatus.SUBMITTED, r.getRecordStatus()));
    }


    /**
     * Reads the content of the test files and returns as a string
     * @param testFileName
     * @return
     * @throws IOException
     */

    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = APIIntegrationTests_BasicMeasurement.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
