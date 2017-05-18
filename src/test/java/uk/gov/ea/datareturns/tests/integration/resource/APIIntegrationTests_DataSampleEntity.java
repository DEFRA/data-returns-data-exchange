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
import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
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
import java.util.List;
import java.util.Map;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class APIIntegrationTests_DataSampleEntity {

    private Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap;
    private SubmissionService submissionService;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_SUCCESS = "json/landfill-success.json";
    private final static String SUBMISSION_FAILURE = "json/landfill-failure.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String[] RECORDS = { "BB001", "BB002", "BB003", "BB004" };

    private static User user;
    private static DatasetEntity dataset;
    private static List<LandfillMeasurementDto> samples;

    @Resource(name="submissionServiceMap")
    private void setSubmissionServiceMap(Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap) {
        this.submissionServiceMap = submissionServiceMap;
    }

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {
        submissionService = submissionServiceMap.get(SubmissionConfiguration.SubmissionServiceProvider.DATA_SAMPLE_V1);

        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }

        user = submissionService.createUser(USER_NAME);

        dataset = new DatasetEntity();
        dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
        dataset.setUser(user);
        submissionService.createDataset(dataset);
        samples = submissionService.parse(readTestFile(SUBMISSION_SUCCESS));
    }

    // Test the basic creation and removal of test records
    @Test public void createTestRemoveRecords() {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.DtoIdentifierPair<>(id));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        Assert.assertEquals(RECORDS.length, records.size());
        for (String id : RECORDS) {
            Assert.assertTrue(submissionService.recordExists(dataset, id));
            Record rec = submissionService.getRecord(dataset, id);
            Assert.assertEquals(Record.RecordStatus.PERSISTED, rec.getRecordStatus());
            submissionService.removeRecord(dataset, id);
            Assert.assertFalse(submissionService.recordExists(dataset, id));
        }
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewUserRecords() {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
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
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
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
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a user identifier. The records
    // should all have a status of PARSED
    @Test public void createNewUserRecordsWithSample() {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        int i = 0;
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(Integer.valueOf(i++).toString(), sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of records and then associate data samples with them
    // as a secondary step
    @Test public void createNewUserRecordsAndAddSample() {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
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
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        records.stream().forEach(r -> Assert.assertEquals(Record.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a valid set of records and submit them
    @Test public void createValidateAndSubmitRecords() {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
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
        List<LandfillMeasurementDto> samples = submissionService.parse(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.VALID).count());
        Assert.assertEquals(3, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid and invalid records and submit them
    @Test public void createAndValidateValidAndInvalidAndSubmitRecords() throws IOException {
        List<LandfillMeasurementDto> samples = submissionService.parse(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        Assert.assertEquals(1, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.SUBMITTED).count());
        Assert.assertEquals(3, records.stream().filter(r -> r.getRecordStatus() == Record.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid records, submit and retrieve them by dataset and dataset/identifier
    @Test public void createAndValidateAndSubmitAndRetrieveRecords() throws IOException {
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
        submissionService.submit(records);
        List<Record> recs = submissionService.retrieve(dataset);
        recs.stream().map(r -> r.getMeasurement()).map(m -> m.getRecord()).forEach(
                r -> Assert.assertEquals(Record.RecordStatus.SUBMITTED, r.getRecordStatus()));

        Record r = recs.stream().findFirst().get();
        String id = r.getIdentifier();

        Record r1 = submissionService.retrieve(dataset, id);
        Assert.assertEquals(r, r1);
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
        InputStream inputStream = APIIntegrationTests_DataSampleEntity.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
