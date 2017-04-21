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

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SubmissionProcessorTests {

    @Inject
    SubmissionProcessor submissionProcessor;

    @Inject
    private TestSettings testSettings;

    public final static String SINGLE_SUCCESSFUL_SUBMISSION = "json/success-single.json";
    public DataSample getSingleSuccessfulSubmissionReference;

    @Before
    public void initialize() {
        getSingleSuccessfulSubmissionReference = setUpReference();
    }

    @Test
    public void setSingleSuccessfulSubmission() throws IOException {
        DataSample sample = (DataSample) submissionProcessor.parse(readTestFile(SINGLE_SUCCESSFUL_SUBMISSION));
        Assert.assertNotNull(sample);
        Assert.assertEquals(sample.getEaId().getIdentifier(), getSingleSuccessfulSubmissionReference.getEaId().getIdentifier());
        Assert.assertEquals(sample.getSiteName().getValue(), getSingleSuccessfulSubmissionReference.getSiteName().getValue());
        Assert.assertEquals(sample.getReturnType().getValue(), getSingleSuccessfulSubmissionReference.getReturnType().getValue());
        Assert.assertEquals(sample.getMonitoringDate().getValue(), getSingleSuccessfulSubmissionReference.getMonitoringDate().getValue());
        Assert.assertEquals(sample.getReturnPeriod().getValue(), getSingleSuccessfulSubmissionReference.getReturnPeriod().getValue());
        Assert.assertEquals(sample.getMonitoringPoint().getValue(), getSingleSuccessfulSubmissionReference.getMonitoringPoint().getValue());
        Assert.assertEquals(sample.getParameter().getValue(), getSingleSuccessfulSubmissionReference.getParameter().getValue());
        Assert.assertEquals(sample.getValue().getValue(), getSingleSuccessfulSubmissionReference.getValue().getValue());
        Assert.assertEquals(sample.getUnit().getValue(), getSingleSuccessfulSubmissionReference.getUnit().getValue());
        Assert.assertEquals(sample.getReferencePeriod().getValue(), getSingleSuccessfulSubmissionReference.getReferencePeriod().getValue());

    }

    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = SubmissionProcessorTests.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private static DataSample setUpReference() {
        DataSample ds = new DataSample();
        ds.setEaId(new EaId("42355"));
        ds.setSiteName(new SiteName("Biffa - Marchington Landfill Site"));
        ds.setReturnType(new ReturnType("Landfill gas borehole"));
        ds.setMonitoringDate(new MonitoringDate("2015-08-05"));
        ds.setReturnPeriod(new ReturnPeriod("Qtr 2 2016"));
        ds.setMonitoringPoint(new MonitoringPoint("Monitor point/ the Mon-Point may be up to 50 chars"));
        ds.setParameter(new Parameter("16-(Thienylmethylene)Androstane-3,17-Diol SVOCS"));
        ds.setValue(new Value("123"));
        ds.setUnit(new Unit("ºC"));
        ds.setReferencePeriod(new ReferencePeriod("Instantaneous"));
        return ds;
    }

}
