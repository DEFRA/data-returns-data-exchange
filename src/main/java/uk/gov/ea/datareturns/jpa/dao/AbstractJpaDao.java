/**
 *
 */
package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.QueryHints;

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
	 * Run a query that expects a List of Strings as a result
	 * 
	 * @param namedQuery the named query to run (should result in a list of Strings being selected)
	 * @return a {@link List} of Strings containing the results
	 */
	protected List<String> stringColumnQuery(String namedQuery) {
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<String> query = em.createNamedQuery(namedQuery, String.class);
			query.setHint(QueryHints.CACHEABLE, "true");
			return query.getResultList();
		} finally {
			em.close();
		}
	}
	

	/**
	 * Create an {@link EntityManager}
	 *
	 * Note, {@link EntityManager} instances should be closed after use!
	 *
	 * @return
	 */
	public EntityManager createEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	public static void configure(final Map<String, String> persistenceProps) {
		entityManagerFactory = Persistence.createEntityManagerFactory("org.gov.ea.datareturns.jpa", persistenceProps);
	}
}
