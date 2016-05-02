package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import uk.gov.ea.datareturns.jpa.entities.Qualifier;

@Repository
public class QualifierDao extends AbstractJpaDao {
	/**
	 * 
	 */
	public QualifierDao() {
		super();
	}
	
	
	/**
	 * Determine if a qualifier with the given name exists
	 * 
	 * @param name the qualifier name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of qualifier names
	 * 
	 * @return a {@link Set} of qualifier names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("Qualifier.findAllNames");
	}

	/**
	 * Get a {@link Qualifier} instance for the given name
	 *
	 * @param name the name of the {@link Qualifier} instance to fetch
	 * @return a {@link Qualifier} for the given name or null if not found.
	 */
	public Qualifier forName(final String name) {
		Qualifier value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<Qualifier> query = em.createNamedQuery("Qualifier.findByName", Qualifier.class);
			query.setParameter("name", name);
			final List<Qualifier> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}