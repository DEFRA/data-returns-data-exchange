package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import uk.gov.ea.datareturns.jpa.entities.ReturnType;

@Repository
public class ReturnTypeDao extends AbstractJpaDao {
	/**
	 * 
	 */
	public ReturnTypeDao() {
		super();
	}
	
	/**
	 * Determine if a Return Type with the given name exists
	 * 
	 * @param name the return type name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of return type names
	 * 
	 * @return a {@link Set} of return type names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("ReturnType.findAllNames");
	}
	
	/**
	 * Get a {@link ReturnType} instance for the given name
	 *
	 * @param name the name of the {@link ReturnType} instance to fetch
	 * @return a {@link ReturnType} for the given name or null if not found.
	 */
	public ReturnType forName(final String name) {
		ReturnType value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<ReturnType> query = em.createNamedQuery("ReturnType.findByName", ReturnType.class);
			query.setParameter("name", name);
			final List<ReturnType> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}