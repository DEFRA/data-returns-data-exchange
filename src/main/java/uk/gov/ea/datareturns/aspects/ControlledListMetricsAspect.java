package uk.gov.ea.datareturns.aspects;

import org.apache.commons.codec.binary.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.util.Environment;

import javax.inject.Inject;
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
    @Inject
    private InfluxDBFacade influxdb;

    @AfterReturning(value = "Pointcuts.transformToPreferred(key)", returning = "preferredEntity")
    public void entityTransformToPreferred(Key key, ControlledListEntity preferredEntity) throws Throwable {
        final long timestamp = System.currentTimeMillis();

        if (key != null && preferredEntity != null) {
            String inputValue = key.getLookup();
            String outputValue = preferredEntity.getName();
            String entityType = preferredEntity.getClass().getSimpleName();
            boolean isPreferred = StringUtils.equals(inputValue, outputValue);
            String usageType = isPreferred ? "preferred" : "alias";

            Point.Builder measurement = Point.measurement(Common.MEASUREMENT_CONTROLLED_LIST_USAGE)
                    .time(timestamp, TimeUnit.MILLISECONDS)
                    .tag(ControlledListUsage.TAG_HOST, Environment.getHostname())
                    .tag(ControlledListUsage.TAG_CONTROLLED_LIST, entityType)
                    .tag(ControlledListUsage.TAG_ITEM_NAME, outputValue)
                    .tag(ControlledListUsage.TAG_USAGE_TYPE, usageType)
                    .addField(ControlledListUsage.FIELD_COUNT, 1);

            if (!isPreferred) {
                measurement.tag(ControlledListUsage.TAG_ITEM_ALIAS, inputValue);
            }
            influxdb.write(measurement.build());
        }
    }
}