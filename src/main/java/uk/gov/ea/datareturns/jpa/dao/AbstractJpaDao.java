/**
 *
 */
package uk.gov.ea.datareturns.jpa.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Sam Gardner-Dell
 *
 */
public abstract class AbstractJpaDao {
	/** Static reference to EntityManagerFactory */
	private static EntityManagerFactory entityManagerFactory;

	/**
	 *
	 */
	public AbstractJpaDao() {
		if (entityManagerFactory == null) {
			throw new RuntimeException("JPA Entity Manager not configured, check database configuration settings");
		}
	}

	/**
	 * Create an {@link EntityManager}
	 *
	 * Note, {@link EntityManager} instances should be closed after use!
	 *
	 * @return
	 */
	protected EntityManager createEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	public static void configure(final Map<String, String> persistenceProps) {
		entityManagerFactory = Persistence.createEntityManagerFactory("org.gov.ea.datareturns.jpa", persistenceProps);
	}
}
