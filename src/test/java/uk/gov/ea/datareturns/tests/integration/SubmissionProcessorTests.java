package uk.gov.ea.datareturns.tests.integration;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.impl.*;
import uk.gov.ea.datareturns.domain.processors.SubmissionProcessor;
import uk.gov.ea.datareturns.domain.result.ValidationErrorType;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SubmissionProcessorTests {

    @Inject
    SubmissionProcessor<DataSample> submissionProcessor;

    @Inject
    private TestSettings testSettings;

    public final static String SINGLE_SUCCESSFUL_SUBMISSION = "json/success-single.json";
    public final static String MULTIPLE_SUCCESSFUL_SUBMISSION = "json/success-multiple.json";
    public final static String SINGLE_FAILURE_SUBMISSION = "json/failure-single.json";
    public final static String MULTIPLE_FAILURE_SUBMISSION = "json/failure-multiple.json";

    public DataSample[] successfulSubmissionReferences;

    @Before
    public void initialize() {
        successfulSubmissionReferences = getSuccessfulSubmissionReferences();
    }

    @Test
    public void testSingleSuccessfulSubmission() throws IOException {
        DataSample[] samples = submissionProcessor.parse(readTestFile(SINGLE_SUCCESSFUL_SUBMISSION));
        Assert.assertNotNull(samples);
        Assert.assertEquals(samples.length, 1);
        Assert.assertEquals(samples[0].getEaId().getIdentifier(), successfulSubmissionReferences[0].getEaId().getIdentifier());
        Assert.assertEquals(samples[0].getSiteName().getValue(), successfulSubmissionReferences[0].getSiteName().getValue());
        Assert.assertEquals(samples[0].getReturnType().getValue(), successfulSubmissionReferences[0].getReturnType().getValue());
        Assert.assertEquals(samples[0].getMonitoringDate().getValue(), successfulSubmissionReferences[0].getMonitoringDate().getValue());
        Assert.assertEquals(samples[0].getReturnPeriod().getValue(), successfulSubmissionReferences[0].getReturnPeriod().getValue());
        Assert.assertEquals(samples[0].getMonitoringPoint().getValue(), successfulSubmissionReferences[0].getMonitoringPoint().getValue());
        Assert.assertEquals(samples[0].getParameter().getValue(), successfulSubmissionReferences[0].getParameter().getValue());
        Assert.assertEquals(samples[0].getValue().getValue(), successfulSubmissionReferences[0].getValue().getValue());
        Assert.assertEquals(samples[0].getUnit().getValue(), successfulSubmissionReferences[0].getUnit().getValue());
        Assert.assertEquals(samples[0].getReferencePeriod().getValue(), successfulSubmissionReferences[0].getReferencePeriod().getValue());
    }

    @Test
    public void testMultipleSuccessfulSubmission() throws IOException {
        DataSample[] samples = submissionProcessor.parse(readTestFile(MULTIPLE_SUCCESSFUL_SUBMISSION));
        Assert.assertNotNull(samples);
        Assert.assertEquals(samples.length, 4);

        Assert.assertEquals(samples[0].getEaId().getIdentifier(), successfulSubmissionReferences[0].getEaId().getIdentifier());
        Assert.assertEquals(samples[0].getSiteName().getValue(), successfulSubmissionReferences[0].getSiteName().getValue());
        Assert.assertEquals(samples[0].getReturnType().getValue(), successfulSubmissionReferences[0].getReturnType().getValue());
        Assert.assertEquals(samples[0].getMonitoringDate().getValue(), successfulSubmissionReferences[0].getMonitoringDate().getValue());
        Assert.assertEquals(samples[0].getReturnPeriod().getValue(), successfulSubmissionReferences[0].getReturnPeriod().getValue());
        Assert.assertEquals(samples[0].getMonitoringPoint().getValue(), successfulSubmissionReferences[0].getMonitoringPoint().getValue());
        Assert.assertEquals(samples[0].getParameter().getValue(), successfulSubmissionReferences[0].getParameter().getValue());
        Assert.assertEquals(samples[0].getValue().getValue(), successfulSubmissionReferences[0].getValue().getValue());
        Assert.assertEquals(samples[0].getUnit().getValue(), successfulSubmissionReferences[0].getUnit().getValue());
        Assert.assertEquals(samples[0].getReferencePeriod().getValue(), successfulSubmissionReferences[0].getReferencePeriod().getValue());

        Assert.assertEquals(samples[1].getEaId().getIdentifier(), successfulSubmissionReferences[1].getEaId().getIdentifier());
        Assert.assertEquals(samples[1].getSiteName().getValue(), successfulSubmissionReferences[1].getSiteName().getValue());
        Assert.assertEquals(samples[1].getReturnType().getValue(), successfulSubmissionReferences[1].getReturnType().getValue());
        Assert.assertEquals(samples[1].getMonitoringDate().getValue(), successfulSubmissionReferences[1].getMonitoringDate().getValue());
        Assert.assertEquals(samples[1].getReturnPeriod().getValue(), successfulSubmissionReferences[1].getReturnPeriod().getValue());
        Assert.assertEquals(samples[1].getMonitoringPoint().getValue(), successfulSubmissionReferences[1].getMonitoringPoint().getValue());
        Assert.assertEquals(samples[1].getParameter().getValue(), successfulSubmissionReferences[1].getParameter().getValue());
        Assert.assertEquals(samples[1].getValue().getValue(), successfulSubmissionReferences[1].getValue().getValue());
        Assert.assertEquals(samples[1].getUnit().getValue(), successfulSubmissionReferences[1].getUnit().getValue());
        Assert.assertEquals(samples[1].getReferencePeriod().getValue(), successfulSubmissionReferences[1].getReferencePeriod().getValue());

        ValidationErrors validationErrors = submissionProcessor.validate(samples);

        Assert.assertTrue(validationErrors.isValid());
    }

    @Test
    public void testSingleFailureSubmission() throws IOException {
        DataSample[] samples = submissionProcessor.parse(readTestFile(SINGLE_FAILURE_SUBMISSION));
        Assert.assertNotNull(samples);
        ValidationErrors validationErrors = submissionProcessor.validate(samples);
        List<ValidationErrorType> errorList = validationErrors.getErrorList();
        Assert.assertEquals(errorList.size(), 6); // Six errors
    }

    @Test
    public void testMultipleFailureSubmission() throws IOException {
        DataSample[] samples = submissionProcessor.parse(readTestFile(MULTIPLE_FAILURE_SUBMISSION));
        Assert.assertEquals(samples.length, 5);
        ValidationErrors validationErrors = submissionProcessor.validate(samples);
        Assert.assertFalse(validationErrors.isValid());

        List<ValidationErrorType> errorList = validationErrors.getErrorList();
        Assert.assertEquals(errorList.size(), 5); // Five errors

        Assert.assertEquals(samples[0].getEaId().getIdentifier(), successfulSubmissionReferences[0].getEaId().getIdentifier());
        Assert.assertEquals(samples[0].getSiteName().getValue(), successfulSubmissionReferences[0].getSiteName().getValue());
        Assert.assertEquals(samples[0].getReturnType().getValue(), successfulSubmissionReferences[0].getReturnType().getValue());
        Assert.assertEquals(samples[0].getMonitoringDate().getValue(), successfulSubmissionReferences[0].getMonitoringDate().getValue());
        Assert.assertEquals(samples[0].getReturnPeriod().getValue(), successfulSubmissionReferences[0].getReturnPeriod().getValue());
        Assert.assertEquals(samples[0].getMonitoringPoint().getValue(), successfulSubmissionReferences[0].getMonitoringPoint().getValue());
        Assert.assertEquals(samples[0].getParameter().getValue(), successfulSubmissionReferences[0].getParameter().getValue());
        Assert.assertEquals(samples[0].getValue().getValue(), successfulSubmissionReferences[0].getValue().getValue());
        Assert.assertEquals(samples[0].getUnit().getValue(), successfulSubmissionReferences[0].getUnit().getValue());
        Assert.assertEquals(samples[0].getReferencePeriod().getValue(), successfulSubmissionReferences[0].getReferencePeriod().getValue());

    }


    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = SubmissionProcessorTests.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private static DataSample[] getSuccessfulSubmissionReferences() {
        DataSample[] ds = new DataSample[4];

        ds[0] = new DataSample();
        ds[0].setEaId(new EaId("42355"));
        ds[0].setSiteName(new SiteName("Biffa - Marchington Landfill Site"));
        ds[0].setReturnType(new ReturnType("Landfill gas borehole"));
        ds[0].setMonitoringDate(new MonitoringDate("2015-08-05"));
        ds[0].setReturnPeriod(new ReturnPeriod("Qtr 2 2016"));
        ds[0].setMonitoringPoint(new MonitoringPoint("Monitor point/ the Mon-Point may be up to 50 chars"));
        ds[0].setParameter(new Parameter("16-(Thienylmethylene)Androstane-3,17-Diol SVOCS"));
        ds[0].setValue(new Value("123"));
        ds[0].setUnit(new Unit("ºC"));
        ds[0].setReferencePeriod(new ReferencePeriod("Instantaneous"));

        ds[1] = new DataSample();
        ds[1].setEaId(new EaId("42355"));
        ds[1].setSiteName(new SiteName("Biffa - Marchington Landfill Site"));
        ds[1].setReturnType(new ReturnType("Landfill gas borehole"));
        ds[1].setMonitoringDate(new MonitoringDate("2015-08-05"));
        ds[1].setReturnPeriod(new ReturnPeriod("Qtr 2 2016"));
        ds[1].setMonitoringPoint(new MonitoringPoint("Monitor point1"));
        ds[1].setParameter(new Parameter("16-(Thienylmethylene)Androstane-3,17-Diol SVOCS"));
        ds[1].setValue(new Value("173"));
        ds[1].setUnit(new Unit("ºC"));
        ds[1].setReferencePeriod(new ReferencePeriod("Instantaneous"));

        return ds;
    }

}
