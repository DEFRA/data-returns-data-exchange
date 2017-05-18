package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.SubmissionConfiguration;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
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
public class APIIntegrationTests_DataSampleEntity_Validations {
    private Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap;
    private SubmissionService submissionService;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_VALUES = "json/landfill-validation-value-txtvalue.json";

    private static final String USER_NAME = "Graham Willis";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";

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
    }

    // Create and validate a set of valid and invalid records
    @Test public void testValidateValueAndTxtValueRecords() throws IOException {
        List<DataSamplePayload> samples = submissionService.parse(readTestFile(SUBMISSION_VALUES));
        List<SubmissionService.DtoIdentifierPair<DataSamplePayload>> list = new ArrayList<>();
        for (DataSamplePayload sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = submissionService.createRecords(dataset, list);
        submissionService.validate(records);
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
        InputStream inputStream = APIIntegrationTests_DataSampleEntity_Validations.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
