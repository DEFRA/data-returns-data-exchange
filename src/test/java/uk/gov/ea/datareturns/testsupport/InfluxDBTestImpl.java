package uk.gov.ea.datareturns.testsupport;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Influx DB test implementation - stores recorded metrics in memory so that integration tests can validate the metrics being recorded
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Primary
@Profile("IntegrationTests")
public class InfluxDBTestImpl implements InfluxDBFacade {
    private Map<String, List<PointData>> points = new HashMap<>();

    public InfluxDBTestImpl() {
    }

    @Override public void write(Point point) {
        PointData datum = new PointData(point);
        List<PointData> data = points.get(datum.measurement);
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(datum);
        points.put(datum.measurement, data);
    }

    public Map<String, List<PointData>> getPointData() {
        return points;
    }

    public void resetData() {
        this.points.clear();
    }

    @Override public boolean isConnected() {
        return true;
    }

    public static class PointData {

        private final String measurement;
        private final Map<String, String> tags;
        private final Long time;
        private final TimeUnit precision;
        private final Map<String, Object> fields;

        public PointData(Point point) {
            this.measurement = (String) getPointField("measurement", point);
            this.tags = (Map<String, String>) getPointField("tags", point);
            this.time = (Long) getPointField("time", point);
            this.precision = (TimeUnit) getPointField("precision", point);
            this.fields = (Map<String, Object>) getPointField("entityfields", point);
        }

        public String getMeasurement() {
            return measurement;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public Long getTime() {
            return time;
        }

        public TimeUnit getPrecision() {
            return precision;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        @Override public String toString() {
            return new ToStringBuilder(this)
                    .append("measurement", measurement)
                    .append("tags", tags)
                    .append("time", time)
                    .append("precision", precision)
                    .append("entityfields", fields)
                    .toString();
        }

        public static Object getPointField(String fieldName, Point point) {
            Field beanField = null;
            try {
                beanField = Point.class.getDeclaredField(fieldName);
                beanField.setAccessible(true);
                return beanField.get(point);
            } catch (Throwable t) {
                throw new RuntimeException("Error extracting point data", t);
            } finally {
                if (beanField != null) {
                    beanField.setAccessible(false);
                }
            }
        }
    }
}
