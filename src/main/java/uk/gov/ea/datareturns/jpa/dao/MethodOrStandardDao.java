package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.MethodOrStandard;

public class MethodOrStandardDao extends AbstractJpaDao {
	private static final MethodOrStandardDao INSTANCE = new MethodOrStandardDao();

	public static MethodOrStandardDao getInstance() {
		return INSTANCE;
	}

	
	/**
	 * Determine if a method or standard with the given name exists
	 * 
	 * @param name the method or standard name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of method or standard names
	 * 
	 * @return a {@link Set} of method or standard names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("MethodOrStandard.findAllNames");
	}
	
	/**
	 * Get a {@link MethodOrStandard} instance for the given name
	 *
	 * @param name the name of the {@link MethodOrStandard} instance to fetch
	 * @return a {@link MethodOrStandard} for the given name or null if not found.
	 */
	public MethodOrStandard forName(final String name) {
		MethodOrStandard value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<MethodOrStandard> query = em.createNamedQuery("MethodOrStandard.findByName", MethodOrStandard.class);
			query.setParameter("name", name);
			final List<MethodOrStandard> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}