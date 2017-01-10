package uk.gov.ea.datareturns.config.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for influxdb
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@ConfigurationProperties(prefix = "spring.influxdb")
public class InfluxDBProperties {
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
