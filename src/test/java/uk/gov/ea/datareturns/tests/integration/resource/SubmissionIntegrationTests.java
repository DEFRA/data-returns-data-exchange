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
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

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
public class SubmissionIntegrationTests {
    @Inject
    SubmissionService<BasicMeasurementDto, BasicMeasurement> submissionService;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_SUCCESS = "json/measurements-success.json";
    //private final static String SUBMISSION_SUCCESS = "json/success-multiple.json";
    //private final static String SUBMISSION_SUCCESS = "json/success-multiple.json";
    //private final static String FAILURE_SUBMISSION = "json/success-multiple.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String DATASET_ID = "SEP2018Q2";
    private static final String[] RECORDS = { "AA0001", "AA002", "AA003" };
    private static final String COMMENT = "Jolly good!";

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
        ValidationErrors validationErrors = submissionService.validate(records);
        Assert.assertTrue(validationErrors.isValid());
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
        InputStream inputStream = SubmissionIntegrationTests.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
