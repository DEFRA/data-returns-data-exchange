package uk.gov.ea.datareturns.config.metrics;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB Facade
 *
 * @author Sam Gardner-Dell
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InfluxDBFacadeImpl implements InfluxDBFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBFacadeImpl.class);
    private final InfluxDBProperties properties;
    private InfluxDB connection;

    /**
     * Create a new {@link InfluxDBFacadeImpl}
     * @param properties configuration {@link InfluxDBProperties} for influxdb
     */
    @Inject
    private InfluxDBFacadeImpl(InfluxDBProperties properties) {
        this.properties = properties;
        if (this.properties.isEnabled()) {
            LOGGER.info("Initialising influxdb connection");
            this.connection = InfluxDBFactory.connect(properties.getUrl(), properties.getUsername(), properties.getPassword());
            this.connection.enableBatch(this.properties.getBatchSize(), this.properties.getFlushTimeoutSeconds(), TimeUnit.SECONDS);

            // Require a connection on startup
            if (!this.isConnected()) {
                throw new BeanCreationException("Unable to connect to influxdb");
            }
            // Ensure the configured database exists.
            boolean createDatabase = !this.connection.describeDatabases().contains(properties.getDatabase());
            String logMsg = createDatabase ? "Creating influxdb database: " : "Using existing influxdb database: ";
            LOGGER.info(logMsg + properties.getDatabase());
            if (createDatabase) {
                this.connection.createDatabase(properties.getDatabase());
            }
        } else {
            LOGGER.warn("Metrics reporting is disabled.");
        }
    }

    /**
     * Store a new record/point.
     * Note that due to batch mode there is no guarantee that the data will be written when this method is called.
     *
     * @param point the {@link Point} to be stored
     */
    public void write(Point point) {
        if (connection != null) {
            connection.write(properties.getDatabase(), properties.getRetentionPolicy(), point);
        }
    }

    /**
     * @return true if the influxdb can be reached, false otherwise.
     */
    public boolean isConnected() {
        try {
            return this.connection.ping().getVersion() != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Shutdown hook to flush and close the influxdb connection
     */
    @PreDestroy
    private void closeConnection() {
        if (this.connection != null) {
            LOGGER.info("Closing influxdb connection");
            if (this.connection.isBatchEnabled()) {
                this.connection.disableBatch();
            }
            this.connection.close();
        }
    }
}