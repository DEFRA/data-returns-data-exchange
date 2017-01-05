package uk.gov.ea.datareturns.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Influxdb database configuration
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableConfigurationProperties(InfluxDBConfiguration.Properties.class)
public class InfluxDBConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBConfiguration.class);

    private InfluxDbFacade influxdb;

    /**
     * Bean factory for influxdb connection facade
     *
     * @param properties
     * @return
     */
    @Bean
    public InfluxDbFacade influxDbConnection(final InfluxDBConfiguration.Properties properties) {
        if (influxdb == null) {
            influxdb = new InfluxDbFacade(properties);
        }
        return influxdb;
    }

    /**
     * InfluxDB Facade
     *
     * @author Sam Gardner-Dell
     */
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public static class InfluxDbFacade {
        private final Properties properties;
        private InfluxDB connection;

        /**
         * Create a new {@link InfluxDbFacade}
         * @param properties configuration {@link Properties} for influxdb
         */
        private InfluxDbFacade(Properties properties) {
            this.properties = properties;
            if (this.properties.isEnabled()) {
                LOGGER.info("Initialising influxdb connection");
                this.connection = InfluxDBFactory.connect(properties.getUrl(), properties.getUsername(), properties.getPassword());
                this.connection.enableBatch(this.properties.batchSize, this.properties.flushTimeoutSeconds, TimeUnit.SECONDS);

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

    /**
     * Configuration properties for influxdb
     *
     * @author Sam Gardner-Dell
     */
    @ConfigurationProperties(prefix = "spring.influxdb")
    public static class Properties {
        private String url;
        private String username;
        private String password;
        private String database;
        private String retentionPolicy;
        private int batchSize = 500;
        private int flushTimeoutSeconds = 300;
        private boolean enabled = false;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(final String database) {
            this.database = database;
        }

        public String getRetentionPolicy() {
            return retentionPolicy;
        }

        public void setRetentionPolicy(final String retentionPolicy) {
            this.retentionPolicy = retentionPolicy;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getFlushTimeoutSeconds() {
            return flushTimeoutSeconds;
        }

        public void setFlushTimeoutSeconds(int flushTimeoutSeconds) {
            this.flushTimeoutSeconds = flushTimeoutSeconds;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}