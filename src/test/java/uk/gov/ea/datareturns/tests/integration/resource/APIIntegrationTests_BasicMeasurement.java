package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.SubmissionConfiguration;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class APIIntegrationTests_BasicMeasurement {

    private SubmissionService submissionService = null;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_SUCCESS = "json/measurements-success.json";
    private final static String SUBMISSION_FAILURE = "json/measurements-fail.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String DATASET_ID = "DatasetEntity name";
    private static final String[] RECORDS = { "AA0001", "AA002", "AA003" };

    private static User user;
    private static DatasetEntity dataset;

    private static List<BasicMeasurementDto> samples;
    private Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap;

    @Resource(name="submissionServiceMap")
    private void setSubmissionServiceMap(Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap) {
        this.submissionServiceMap = submissionServiceMap;
    }

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {
        submissionService = submissionServiceMap.get(SubmissionConfiguration.SubmissionServiceProvider.BASIC_VERSION_1);

        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }

        user = submissionService.createUser(USER_NAME);
        dataset = new DatasetEntity();
        dataset.setUser(user);
        submissionService.createDataset(dataset);
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
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        submissionService.createDataset(dataset);
        Assert.assertEquals(dataset.getUser().getIdentifier(), User.SYSTEM);
    }

    @Test
    public void createUserManagedDatasetAutonamed() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setUser(user);
        submissionService.createDataset(dataset);

        Assert.assertEquals(dataset.getUser().getIdentifier(), USER_NAME);
        List<DatasetEntity> datasets = submissionService.getDatasets(user);
        Assert.assertEquals(2, datasets.size());
    }

    @Test
    public void createUserManagedDatasetNamed() {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setUser(user);
        dataset.setIdentifier(DATASET_ID);
        submissionService.createDataset(dataset);

        List<DatasetEntity> datasets = submissionService.getDatasets(user);
        Assert.assertEquals(2, datasets.size());

        DatasetEntity ds = submissionService.getDataset(DATASET_ID, user);

        Assert.assertEquals(ds.getUser().getIdentifier(), USER_NAME);
        Assert.assertEquals(ds.getIdentifier(), DATASET_ID);

        submissionService.removeDataset(DATASET_ID, user);
        datasets = submissionService.getDatasets(user);
        Assert.assertEquals(1, datasets.size());
    }

    @Test public void createTestRemoveRecords() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DtoIdentifierPair<>(id));
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
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DtoIdentifierPair(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewSystemRecords() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DtoIdentifierPair());
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a system identifier. The records
    // should all have a status of PARSED
    @Test public void createNewSystemRecordsWithSample() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a user identifier. The records
    // should all have a status of PARSED
    @Test public void createNewUserRecordsWithSample() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        int i = 0;
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(Integer.valueOf(i++).toString(), sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of records and then associate data samples with them
    // as a secondary step
    @Test public void createNewUserRecordsAndAddSample() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DtoIdentifierPair(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PERSISTED, r.getRecordStatus()));

        list = new ArrayList<>();
        for (int i = 0; i < Math.min(RECORDS.length, samples.size()); i++) {
            list.add(new SubmissionService.DtoIdentifierPair(RECORDS[i], samples.get(i)));
        }
        records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create and validate a set of valid records
    @Test public void createAndValidateValidRecords() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a valid set of records and submit them
    @Test public void createValidateAndSubmitRecords() {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
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
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        Assert.assertEquals(5, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.VALID).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid and invalid records and submit them
    @Test public void createAndValidateValidAndInvalidAndSubmitRecords() throws IOException {
        List<BasicMeasurementDto> samples = submissionService.parse(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        Assert.assertEquals(5, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.SUBMITTED).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid records, submit and retrieve them by dataset
    @Test public void createAndValidateAndSubmitAndRetrieveRecords() throws IOException {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        List<Record> recs = submissionService.retrieve(dataset);
        recs.stream().map(r -> r.getMeasurement()).map(m -> m.getRecord()).forEach(
                r -> Assert.assertEquals(Record.RecordStatus.SUBMITTED, r.getRecordStatus()));
    }

    // Create a valid set of records and then change one of them
    @Test public void createAndValidateAndChangeRecords() throws IOException {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        List<Record> recs = submissionService.retrieve(dataset);
        BasicMeasurementDto dto = new BasicMeasurementDto();
        dto.setParameter("1,1-Dichloropropene");
        dto.setValue("9999");
        SubmissionService.DtoIdentifierPair pair = new SubmissionService.DtoIdentifierPair(recs.get(1).getIdentifier(), dto);

        records = submissionService.createRecords(dataset, Collections.singletonList(pair));
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
        Assert.assertEquals(1, records.size());
    }

    // Test that resetting a submitted record has no effect
    @Test public void testResetSubmittedRecordsHasNoEffect() throws IOException {
        List<SubmissionService.DtoIdentifierPair<BasicMeasurementDto>> list = new ArrayList<>();
        for (BasicMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        List<Record> recs = submissionService.retrieve(dataset);
        BasicMeasurementDto dto = new BasicMeasurementDto();
        dto.setParameter("1,1-Dichloropropene");
        dto.setValue("9999");
        SubmissionService.DtoIdentifierPair pair = new SubmissionService.DtoIdentifierPair(recs.get(1).getIdentifier(), dto);
        records = submissionService.createRecords(dataset, Collections.singletonList(pair));
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(Record.RecordStatus.SUBMITTED, records.get(0).getRecordStatus());
        Assert.assertNotEquals(9999, ((BasicMeasurement)records.get(0).getMeasurement()).getNumericValue());
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
