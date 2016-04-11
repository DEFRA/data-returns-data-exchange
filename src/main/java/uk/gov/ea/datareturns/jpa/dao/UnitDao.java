package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.Unit;

public class UnitDao extends AbstractJpaDao {
	private static final UnitDao INSTANCE = new UnitDao();

	public static UnitDao getInstance() {
		return INSTANCE;
	}

	/**
	 * Get a {@link Unit} instance for the given name
	 *
	 * @param name the name of the {@link Unit} instance to fetch
	 * @return a {@link Unit} for the given name or null if not found.
	 */
	public Unit forName(final String name) {
		Unit value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<Unit> query = em.createNamedQuery("Unit.findByName", Unit.class);
			query.setParameter("name", name);
			final List<Unit> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}