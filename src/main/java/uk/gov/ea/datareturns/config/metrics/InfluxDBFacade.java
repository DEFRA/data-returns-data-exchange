package uk.gov.ea.datareturns.config.metrics;

import org.influxdb.dto.Point;

/**
 * InfluxDB Facade
 */
public interface InfluxDBFacade {
    /**
     * Write a data point to influx db
     * @param point the {@link Point} to be written
     */
    void write(Point point);

    /**
     * Is the connection to InfluxDB active/connected
     * @return true if connected, false otherwise.
     */
    boolean isConnected();
}
