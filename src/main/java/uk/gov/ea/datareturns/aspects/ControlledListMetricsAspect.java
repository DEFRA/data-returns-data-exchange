package uk.gov.ea.datareturns.aspects;

import org.apache.commons.codec.binary.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.InfluxDBConfiguration;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

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
    private InfluxDBConfiguration.InfluxDbFacade influxdb;

    @AfterReturning(value = "Pointcuts.transformToPreferred(key)", returning = "preferredEntity")
    public void entityTransformToPreferred(Key key, ControlledListEntity preferredEntity) throws Throwable {
        final long timestamp = System.currentTimeMillis();

        if (key != null && preferredEntity != null) {
            String inputValue = key.getLookup();
            String outputValue = preferredEntity.getName();
            String entityType = preferredEntity.getClass().getSimpleName();
            boolean isPreferred = StringUtils.equals(inputValue, outputValue);
            String usageType = isPreferred ? "preferred" : "alias";

            Point.Builder measurement = Point.measurement("controlled_list_usage")
                    .time(timestamp, TimeUnit.MILLISECONDS)
                    .tag("controlled_list", entityType)
                    .tag("item_name", outputValue)
                    .tag("usage_type", usageType)
                    .addField("count", 1);

            if (!isPreferred) {
                measurement.tag("item_alias", inputValue);
            }
            influxdb.write(measurement.build());
        }
    }
}