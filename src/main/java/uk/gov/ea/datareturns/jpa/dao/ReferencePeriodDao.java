package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import uk.gov.ea.datareturns.jpa.entities.ReferencePeriod;

@Repository
public class ReferencePeriodDao extends AbstractJpaDao {
	/**
	 * 
	 */
	public ReferencePeriodDao() {
		super();
	}
	
	/**
	 * Determine if a reference period with the given name exists
	 * 
	 * @param name the reference period name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of reference period names
	 * 
	 * @return a {@link Set} of reference period names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("ReferencePeriod.findAllNames");
	}
	
	/**
	 * Get a {@link ReferencePeriod} instance for the given name
	 *
	 * @param name the name of the {@link ReferencePeriod} instance to fetch
	 * @return a {@link ReferencePeriod} for the given name or null if not found.
	 */
	public ReferencePeriod forName(final String name) {
		ReferencePeriod value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<ReferencePeriod> query = em.createNamedQuery("ReferencePeriod.findByName", ReferencePeriod.class);
			query.setParameter("name", name);
			final List<ReferencePeriod> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}