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
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

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
    private final static String SUBSTITUTIONS = "json/landfill-substitutions.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String[] RECORDS = { "BB001", "BB002", "BB003", "BB004" };

    private static User user;
    private static DatasetEntity dataset;
    private static List<DataSamplePayload> samples;

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
        samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_SUCCESS));
    }

    // Test the basic creation and removal of test records
    @Test public void createTestRemoveRecords() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.ObservationIdentifierPair<>(id));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        Assert.assertEquals(RECORDS.length, recordEntities.size());
        for (String id : RECORDS) {
            Assert.assertTrue(submissionService.recordExists(dataset, id));
            RecordEntity rec = submissionService.getRecord(dataset, id);
            Assert.assertEquals(RecordEntity.RecordStatus.PERSISTED, rec.getRecordStatus());
            submissionService.removeRecord(dataset, id);
            Assert.assertFalse(submissionService.recordExists(dataset, id));
        }
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewUserRecords() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.ObservationIdentifierPair(id));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewSystemRecords() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.ObservationIdentifierPair());
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PERSISTED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a system identifier. The records
    // should all have a status of PARSED
    @Test public void createNewSystemRecordsWithSample() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a user identifier. The records
    // should all have a status of PARSED
    @Test public void createNewUserRecordsWithSample() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        int i = 0;
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(Integer.valueOf(i++).toString(), sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create a set of records and then associate data samples with them
    // as a secondary step
    @Test public void createNewUserRecordsAndAddSample() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (String id : RECORDS) {
            list.add(new SubmissionService.ObservationIdentifierPair(id));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PERSISTED, r.getRecordStatus()));

        list = new ArrayList<>();
        for (int i = 0; i < Math.min(RECORDS.length, samples.size()); i++) {
            list.add(new SubmissionService.ObservationIdentifierPair(RECORDS[i], samples.get(i)));
        }
        recordEntities = submissionService.createRecords(dataset, list);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.PARSED, r.getRecordStatus()));
    }

    // Create and validate a set of valid records
    @Test public void createAndValidateValidRecords() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a valid set of records and submit them
    @Test public void createValidateAndSubmitRecords() {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
        submissionService.submit(recordEntities);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.SUBMITTED, r.getRecordStatus()));
    }

    @Test public void createValidateAndDetermineSubstitutions() throws IOException {
        List<DataSamplePayload> samples = submissionService.parseJsonArray(readTestFile(SUBSTITUTIONS));
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
        submissionService.evaluateSubstitutes(recordEntities);
        //recordEntities.stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.SUBMITTED, r.getRecordStatus()));
    }


    // Create and validate a set of valid and invalid records
    @Test public void createAndValidateValidAndInvalidRecords() throws IOException {
        List<DataSamplePayload> samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        Assert.assertEquals(1, recordEntities.stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID).count());
        Assert.assertEquals(3, recordEntities.stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid and invalid records and submit them
    @Test public void createAndValidateValidAndInvalidAndSubmitRecords() throws IOException {
        List<DataSamplePayload> samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE));
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        submissionService.submit(recordEntities);
        Assert.assertEquals(1, recordEntities.stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.SUBMITTED).count());
        Assert.assertEquals(3, recordEntities.stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid records, submit and retrieve them by dataset and dataset/identifier
    @Test public void createAndValidateAndSubmitAndRetrieveRecords() throws IOException {
        List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.ObservationIdentifierPair(sample));
        }
        List<RecordEntity> recordEntities = submissionService.createRecords(dataset, list);
        submissionService.validate(recordEntities);
        submissionService.submit(recordEntities);
        List<RecordEntity> recs = submissionService.retrieve(dataset);
        recs.stream().map(r -> r.getAbstractObservation()).map(m -> m.getRecordEntity()).forEach(
                r -> Assert.assertEquals(RecordEntity.RecordStatus.SUBMITTED, r.getRecordStatus()));

        RecordEntity r = recs.stream().findFirst().get();
        String id = r.getIdentifier();

        RecordEntity r1 = submissionService.retrieve(dataset, id);
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
