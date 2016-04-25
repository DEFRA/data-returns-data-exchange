/**
 *
 */
package uk.gov.ea.datareturns.jpa.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	// Cached lists
	Map<String, Set<String>> cachedStringSets = Collections.synchronizedMap(new HashMap<>());
	
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
	protected <T extends Collection<String>> T stringColumnQuery(String namedQuery, T target) {
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<String> query = em.createNamedQuery(namedQuery, String.class);
			query.setHint(QueryHints.CACHEABLE, "true");
			target.addAll(query.getResultList());
			return target;
		} finally {
			em.close();
		}
	}
	
	
	protected Set<String> cachedColumnQuery(String namedQuery) {
		Set<String> cachedSet = cachedStringSets.get(namedQuery);
		if (cachedSet == null) {
			synchronized (cachedStringSets) {
				cachedSet = cachedStringSets.get(namedQuery);
				if (cachedSet == null) {
					cachedSet = stringColumnQuery(namedQuery, new HashSet<>());
				}
				cachedStringSets.put(namedQuery, cachedSet);
			}
		}
		return Collections.unmodifiableSet(cachedSet);
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
