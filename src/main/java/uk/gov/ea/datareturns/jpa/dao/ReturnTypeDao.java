package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.ReturnType;

public class ReturnTypeDao extends AbstractJpaDao {
	private static final ReturnTypeDao INSTANCE = new ReturnTypeDao();

	public static ReturnTypeDao getInstance() {
		return INSTANCE;
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