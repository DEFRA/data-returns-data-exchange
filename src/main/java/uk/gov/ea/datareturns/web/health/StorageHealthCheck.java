package uk.gov.ea.datareturns.web.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.storage.StorageException;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;

import javax.inject.Inject;

/**
 * Checks the health of the configured Storage Provider.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class StorageHealthCheck implements HealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageHealthCheck.class);
    private final StorageProvider storageProvider;

    /**
     * Create a new {@link StorageHealthCheck} for the specified {@link StorageProvider} instance
     *
     * @param storageProvider the {@link StorageProvider} to act on to determine health status
     */
    @Inject
    public StorageHealthCheck(final StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    /**
     * Checks the storage system is healthy
     *
     * @return a {@link Health} object describing the current status of the system
     */
    @Override
    public Health health() {
        LOGGER.info("Running storage health check");
        Health health;
        try {
            if (this.storageProvider.healthy()) {
                health = Health.up().build();
            } else {
                health = Health.down().build();
            }
        } catch (final StorageException e) {
            health = Health.down(e).build();
        }
        return health;
    }
}