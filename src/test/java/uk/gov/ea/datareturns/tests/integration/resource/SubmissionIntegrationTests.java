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
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleSubmission;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.Datum;
import uk.gov.ea.datareturns.domain.model.fields.impl.Comments;
import uk.gov.ea.datareturns.domain.processors.SubmissionProcessor;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.tests.integration.model.SubmissionProcessorTests;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SubmissionIntegrationTests {
    @Inject SubmissionService submissionService;
    @Inject SubmissionProcessor<DataSample> submissionProcessor;
    @Inject private TestSettings testSettings;

    private final static String SUCCESSFUL_SUBMISSION = "json/success-multiple.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String DATASET_ID = "SEP2018Q2";
    private static final String[] RECORDS = { "AA0001", "AA002", "AA003" };
    private static final String COMMENT = "Jolly good!";

    private static User user;
    private static Dataset dataset;

    // Remove any old data and set a user and dataset for use in the tests
    @Before
    public void init() {
        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }
        user = submissionService.createUser(USER_NAME);
        dataset = submissionService.createDataset(user);
    }

    @Test
    public void validationAndSubmission() throws IOException {
        List<DataSample> samples = getValidDataSamples();
        submissionService.submit(dataset, samples);
        //Assert.assertEquals(4, count);
    }

    @Test
    public void validationAndSubmissionAndRemoval() throws IOException {
        List<DataSample> samples = getValidDataSamples();
        submissionService.submit(dataset, samples);
        List<Dataset> datasets = submissionService.getDatasets(user);
        List<Record> records = submissionService.getRecords(datasets.get(0));
        Assert.assertEquals(4, records.size());
        submissionService.removeDataset(datasets.get(0).getIdentifier());
        datasets = submissionService.getDatasets(user);
        Assert.assertEquals(0, datasets.size());
    }


    @Test
    public void validationAndSubmissionAndChangeRemoval() throws IOException {
        List<DataSample> samples = getValidDataSamples();
        // For each sample create a user defined record
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < samples.size() + 1; i++) {
            ids.add("USR_" + i++);
        }
        List<Record> records = submissionService.createRecords(dataset, ids);

        // Get the second new record and add a sample submission
        Record record = submissionService.getRecord(dataset, ids.get(1));
        submissionService.submit(record, samples.get(1));

        // Changed the sample comments.
        // The changed record will be validated
        DataSampleSubmission changedSample = (DataSampleSubmission)record.getSubmission();
        Datum datum = changedSample.getDatum();

        //DataSample sample
        //changedSample.setComments(COMMENT);
        //submissionService.submit(record, changedSample);

        // Get from record
        //Record newRecord = submissionService.getRecord(ids.get(1));
        //String comment = ((DataSampleSubmission) newRecord.getSubmission()).getComments();
        //Assert.assertEquals(COMMENT, comment);
    }

    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = SubmissionProcessorTests.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private List<DataSample> getValidDataSamples() throws IOException {
        List<DataSample> samples = submissionProcessor.parse(readTestFile(SUCCESSFUL_SUBMISSION));
        ValidationErrors validationErrors = submissionProcessor.validate(samples);
        Assert.assertTrue(validationErrors.isValid());
        return samples;
    }

}
