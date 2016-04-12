package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.ReferencePeriod;

public class ReferencePeriodDao extends AbstractJpaDao {
	private static final ReferencePeriodDao INSTANCE = new ReferencePeriodDao();

	public static ReferencePeriodDao getInstance() {
		return INSTANCE;
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