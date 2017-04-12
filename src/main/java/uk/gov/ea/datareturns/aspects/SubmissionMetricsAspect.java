package uk.gov.ea.datareturns.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;
import uk.gov.ea.datareturns.util.Environment;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * Aspect to gather metrics regarding the submission of data to the downstream system.
 *
 * @author Sam Gardner-Dell
 */
@Aspect
@Configurable
@Component
public class SubmissionMetricsAspect {
    /** influx database connection */
    private final InfluxDBFacade influxDb;

    /**
     * Instantiates a new {@link SubmissionMetricsAspect}
     *
     * @param influxDb the influx database connection
     */
    @Inject
    public SubmissionMetricsAspect(final InfluxDBFacade influxDb) {
        this.influxDb = influxDb;
    }

    @Around(value = "Pointcuts.dataSubmission(originatorEmail, originatorFilename, eaId, returnsCSVFile)", argNames = "pjp,originatorEmail,originatorFilename,eaId,returnsCSVFile")
    public Object writeOutputFileAdvice(ProceedingJoinPoint pjp, String originatorEmail, String originatorFilename, String eaId,
            File returnsCSVFile) throws Throwable {

        try {
            // Execute the join point (actually start writing data to an output file)
            return pjp.proceed();
        } finally {
            // TODO: Rework this when doing backend API architecture changes - quick fix to count number of records submitted to the downstream system
            final long recordCount = Math.max(0, Files.lines(returnsCSVFile.toPath()).count() - 1);
            final long timestamp = System.currentTimeMillis();
            influxDb.write(
                    Point.measurement(MetricsConstants.Common.MEASUREMENT_SUBMISSIONS)
                            .time(timestamp, TimeUnit.MILLISECONDS)
                            .tag(MetricsConstants.SubmissionEvent.TAG_HOST, Environment.getHostname())
                            .tag(MetricsConstants.SubmissionEvent.TAG_EA_ID, eaId)
                            .addField(MetricsConstants.SubmissionEvent.FIELD_RECORD_COUNT, recordCount)
                            .build());
        }
    }
}