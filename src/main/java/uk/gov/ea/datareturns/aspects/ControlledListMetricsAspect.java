package uk.gov.ea.datareturns.aspects;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.util.Environment;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static uk.gov.ea.datareturns.aspects.MetricsConstants.Common;
import static uk.gov.ea.datareturns.aspects.MetricsConstants.ControlledListUsage;

/**
 * Aspect to gather metrics from the data layer regarding alias cache hits.
 *
 * @author Sam Gardner-Dell
 */
@Aspect
@Configurable
@Component
public class ControlledListMetricsAspect {
    /** class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListMetricsAspect.class);
    /** thread-local store for controlled list usage data generated when transforming the data and writing output files */
    private static final ThreadLocal<Map<String, ControlledListUsageData>> USAGE_DATA = new ThreadLocal<>();
    /** thread-local storage to track which record is currently being transformed/output */
    private static final ThreadLocal<DataSample> RECORD_THREAD_LOCAL = new ThreadLocal<>();
    /** influx database connection */
    private final InfluxDBFacade influxDb;

    /**
     * Instantiates a new {@link ControlledListMetricsAspect}
     *
     * @param influxDb the influx database connection
     */
    @Inject
    public ControlledListMetricsAspect(final InfluxDBFacade influxDb) {
        this.influxDb = influxDb;
    }

    /**
     * Around-advice to hook the start/end of the process to write an output file.
     *
     * This hook initialises the USAGE_DATA thread local before writing an output file and serializes the USAGE_DATA once
     * an output file has been written.  Subsequent advice methods below are responsible for populating the USAGE_DATA.
     *
     *
     * @param pjp the {@link ProceedingJoinPoint} object used to control access to the join-point
     * @param records the records the list of {@link DataSample} records to be written to the output file.
     * @param csvFile the csv file being written
     * @return the return value from execution of the join point.
     * @throws Throwable any error which may result from executing the join point.
     */
    @Around(value = "Pointcuts.writeOutputFile(records, csvFile)", argNames = "pjp,records,csvFile")
    public Object writeOutputFileAdvice(ProceedingJoinPoint pjp, List<DataSample> records, File csvFile) throws Throwable {
        // Initialise usage data.
        if (USAGE_DATA.get() != null) {
            LOGGER.error("Usage data should not be present in the ThreadLocal, but was found");
        }
        USAGE_DATA.set(new HashMap<>());
        try {
            // Execute the join point (actually start writing data to an output file)
            return pjp.proceed();
        } finally {
            // Once output file has been written, persist the usage data and remove the thread-local usage data store
            Map<String, ControlledListUsageData> usageData = USAGE_DATA.get();
            USAGE_DATA.remove();

            final long timestamp = System.currentTimeMillis();
            // Copy information from stored ControlledListUsageData into influxDb points
            usageData.values().stream().map(data -> {
                Point.Builder measurement = Point.measurement(Common.MEASUREMENT_CONTROLLED_LIST_USAGE)
                        .time(timestamp, TimeUnit.MILLISECONDS);
                // Copy all tags stored in ControlledListUsageData into the point
                data.tags.entrySet().forEach(entry -> measurement.tag(entry.getKey(), entry.getValue()));
                measurement.tag(ControlledListUsage.TAG_HOST, Environment.getHostname())
                        .addField(ControlledListUsage.FIELD_USAGE_COUNT, data.usageCount);
                return measurement.build();
            }).forEach(influxDb::write);
        }
    }

    /**
     * Around-advice to hook the start/end of an individual record being written out
     *
     * This hook is used to store a reference to the current record ({@link DataSample}) being transformed/written.
     *
     * @param pjp the {@link ProceedingJoinPoint} object used to control access to the join-point
     * @param record the current {@link DataSample} record being transformed/written
     * @return the return value from execution of the join point.
     * @throws Throwable any error which may result from executing the join point.
     */
    @Around(value = "Pointcuts.writeDataSampleRecord(*, record)", argNames = "pjp,record")
    public Object writeDataSampleRecord(ProceedingJoinPoint pjp, DataSample record) throws Throwable {
        if (RECORD_THREAD_LOCAL.get() != null) {
            LOGGER.error("DataSample record should not be present in the ThreadLocal, but was found");
        }
        RECORD_THREAD_LOCAL.set(record);
        try {
            return pjp.proceed();
        } finally {
            RECORD_THREAD_LOCAL.remove();
        }
    }

    /**
     * After-returning advice to hook the process by which each entity is transformed to the preferred usage.
     *
     * Uses context information from the hooks above to build a store of controlled list usage data in memory.
     *
     * @param key the DAO {@link Key} object used to perform the lookup
     * @param preferredEntity the preferred entity returned by the DAO for the key
     * @throws Throwable any error which may result from executing the join point.
     */
    @AfterReturning(pointcut = "Pointcuts.transformToPreferred(key)", returning = "preferredEntity", argNames = "key,preferredEntity")
    public void entityTransformToPreferred(Key key, ControlledListEntity preferredEntity) throws Throwable {
        DataSample currentRecord = RECORD_THREAD_LOCAL.get();
        Map<String, ControlledListUsageData> usageData = USAGE_DATA.get();

        // If the usage data or current record is null, then the DAO function to transform to the preferred value has not invoked in
        // relation to a transformation when writing a record to an output CSV - don't write these instances to metrics
        if (usageData != null && currentRecord != null && key != null && preferredEntity != null) {
            String currentEaId = Optional.ofNullable(currentRecord.getEaId().getEntity()).map(UniqueIdentifier::getName).orElse("Unknown");
            String inputValue = key.getLookup();
            String outputValue = preferredEntity.getName();
            String entityType = preferredEntity.getClass().getSimpleName();
            boolean isPreferred = StringUtils.equals(inputValue, outputValue);
            String usageType = isPreferred ? "preferred" : "alias";

            // Build tag/key data to represent this usage of controlled list
            ControlledListUsageData data = new ControlledListUsageData();
            data.tag(ControlledListUsage.TAG_HOST, Environment.getHostname())
                    .tag(ControlledListUsage.TAG_EA_ID, currentEaId)
                    .tag(ControlledListUsage.TAG_CONTROLLED_LIST, entityType)
                    .tag(ControlledListUsage.TAG_ITEM_NAME, outputValue)
                    .tag(ControlledListUsage.TAG_USAGE_TYPE, usageType);
            if (!isPreferred) {
                data.tag(ControlledListUsage.TAG_ITEM_ALIAS, inputValue);
            }

            // Has there already been a usage of this tag/key data in this file?
            ControlledListUsageData existingData = usageData.get(data.tagKey());
            if (existingData != null) {
                data = existingData;
            }

            // Increment the usage count
            data.incrementUsageCount();
            usageData.put(data.tagKey(), data);
        }
    }

    /**
     * Internal class used for temporary store of controlled list usage data.
     */
    private static class ControlledListUsageData {
        private final Map<String, String> tags = new LinkedHashMap<>();
        private int usageCount;

        /**
         * Add a tag value
         *
         * @param name the tag name
         * @param value the value for the tag
         * @return a reference to this {@link ControlledListUsageData} to enable chained calls to .tag()
         */
        private ControlledListUsageData tag(String name, String value) {
            tags.put(name, value);
            return this;
        }

        /**
         * Create a map key based on the tags present
         *
         * @return a map key identifying this series
         */
        private String tagKey() {
            return tags.values().toString();
        }

        /**
         * Increment the usage count for this usage instance.
         */
        private void incrementUsageCount() {
            usageCount++;
        }
    }
}