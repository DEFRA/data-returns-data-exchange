/**
 *
 */
package uk.gov.ea.datareturns.unittests;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import uk.gov.ea.datareturns.health.StorageHealthCheck;
import uk.gov.ea.datareturns.storage.StorageException;
import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Tests the {@link StorageHealthCheck} implementation interacts with the {@link StorageProvider} interface
 * correctly.
 *
 * @author Sam Gardner-Dell
 */
public class StorageHealthCheckTests {

	@Test
	public void testStorageHealthCheckHealthy() {
		final StorageHealthCheck healthCheck = new StorageHealthCheck(new NoOpStorageProvider(true));
		final Health health = healthCheck.health();
		Assert.assertTrue(health.getStatus().equals(Status.UP));
	}

	@Test
	public void testStorageHealthCheckUnhealthy() {
		final StorageHealthCheck healthCheck = new StorageHealthCheck(new NoOpStorageProvider(false));
		final Health health = healthCheck.health();
		Assert.assertTrue(health.getStatus().equals(Status.DOWN));
	}

	@Test
	public void testStorageHealthCheckUnhealthyException() {
		final StorageHealthCheck healthCheck = new StorageHealthCheck(new NoOpStorageProvider(new StorageException("Test exception")));
		final Health health = healthCheck.health();
		Assert.assertTrue(health.getStatus().equals(Status.DOWN));
	}

	/**
	 * Simple no-operation {@link StorageProvider} implementation for testing the {@link StorageHealthCheck} only
	 *
	 * @author Sam Gardner-Dell
	 */
	private static class NoOpStorageProvider implements StorageProvider {
		private boolean healthy;

		private StorageException exceptionToThrow;

		public NoOpStorageProvider(final boolean healthy) {
			this.healthy = healthy;
		}

		public NoOpStorageProvider(final StorageException exceptionToThrow) {
			this.exceptionToThrow = exceptionToThrow;
		}

		@Override
		public String storeTemporaryData(final File file) throws StorageException {
			return null;
		}

		@Override
		public StoredFile retrieveTemporaryData(final String fileKey) throws StorageException {
			return null;
		}

		@Override
		public String moveToAuditStore(final String fileKey, final Map<String, String> metadata) throws StorageException {
			return null;
		}

		@Override
		public boolean healthy() throws StorageException {
			if (this.exceptionToThrow != null) {
				throw this.exceptionToThrow;
			}
			return this.healthy;
		}

	}
}
