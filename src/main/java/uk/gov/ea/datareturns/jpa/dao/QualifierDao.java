package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.Qualifier;

public class QualifierDao extends AbstractJpaDao {
	private static final QualifierDao INSTANCE = new QualifierDao();

	public static QualifierDao getInstance() {
		return INSTANCE;
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