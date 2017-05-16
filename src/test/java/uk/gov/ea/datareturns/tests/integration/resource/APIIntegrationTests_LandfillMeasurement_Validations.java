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
import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.LandfillMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;

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
public class APIIntegrationTests_LandfillMeasurement_Validations {
    @Inject
    SubmissionService<LandfillMeasurementDto, LandfillMeasurement, LandfillMeasurementMvo> landfillSubmissionService;

    @Inject private TestSettings testSettings;

    private final static String SUBMISSION_VALUES = "json/landfill-validation-value-txtvalue.json";

    private static final String USER_NAME = "Graham Willis";

    private static User user;
    private static Dataset dataset;
    private static List<LandfillMeasurementDto> samples;

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {
        if (landfillSubmissionService.getUser(USER_NAME) != null) {
            landfillSubmissionService.removeUser(USER_NAME);
        }
        user = landfillSubmissionService.createUser(USER_NAME);
        dataset = landfillSubmissionService.createDataset(user);
    }

    // Create and validate a set of valid and invalid records
    @Test public void testValidateValueAndTxtValueRecords() throws IOException {
        List<LandfillMeasurementDto> samples = landfillSubmissionService.parse(readTestFile(SUBMISSION_VALUES));
        List<SubmissionService.DtoIdentifierPair<LandfillMeasurementDto>> list = new ArrayList<>();
        for (LandfillMeasurementDto sample : samples) {
            list.add(new SubmissionService.DtoIdentifierPair(sample));
        }
        List<Record> records = landfillSubmissionService.createRecords(dataset, list);
        landfillSubmissionService.validate(records);
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
        InputStream inputStream = APIIntegrationTests_LandfillMeasurement_Validations.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
