package uk.gov.ea.datareturns.tests.integration.metrics;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.testsupport.InfluxDBTestImpl;

import javax.inject.Inject;
import java.io.InputStream;

/**
 * Provides common functionality for all metrics reporting tests
 *
 * @author Sam Gardner-Dell
 */
public abstract class AbstractMetricsTests {
    @Inject
    private FileUploadProcessor processor;
    @Inject
    private InfluxDBFacade facade;

    /** We need a concrete reference to the InlfuxDBStub to re trieve the metrics that have been recorded */
    protected InfluxDBTestImpl stub;

    /**
     * Before each test, reset the metrics data
     */
    @Before
    public void setup() {
        if (!(facade instanceof InfluxDBTestImpl)) {
            throw new RuntimeException("Configuration Error: Metrics tests requires influxdb facade to be injected with an InfluxDBStub");
        }
        this.stub = (InfluxDBTestImpl) facade;
        this.stub.resetData();
    }

    /**
     * Upload a file to the service
     *
     * @param testFile the test file to be uploaded
     * @return the {@link DataExchangeResult} returned by the REST api
     */
    protected DataExchangeResult uploadFile(final String testFile) {
        final String path = String.format("/testfiles/metrics/%s/%s", this.getClass().getSimpleName(), testFile);
        final InputStream inputStream = AbstractMetricsTests.class.getResourceAsStream(path);

        processor.setClientFilename("ProcessorIntegrationTests.csv");
        processor.setInputStream(inputStream);

        DataExchangeResult result = null;
        try {
            result = processor.process();
        } catch (final ProcessingException e) {
            Assertions.fail("Processor exception thrown", e);
        }
        return result;
    }
}