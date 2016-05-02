package uk.gov.ea.datareturns.health;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Checks the health of the configured Storage Provider
 * 
 * @author Sam Gardner-Dell
 */
@Component
public class StorageHealthCheck implements HealthIndicator {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageHealthCheck.class);

	@Inject
    private StorageProvider storageProvider;

    public StorageHealthCheck() {
    }
    
    public StorageHealthCheck(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

	@Override
	public Health health() {
		LOGGER.info("Running storage health check");
		Health health = null;
		
		try {
			if (storageProvider.healthy()) {
				health = Health.up().build();
			} else {
				health = Health.down().build();
			}
		} catch (StorageException e) {
			health = Health.down(e).build();
		}
		return health;
	}
}
