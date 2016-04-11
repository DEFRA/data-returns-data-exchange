package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.Parameter;

public class ParameterDao extends AbstractJpaDao {
	private static final ParameterDao INSTANCE = new ParameterDao();

	public static ParameterDao getInstance() {
		return INSTANCE;
	}

	/**
	 * Get a {@link Parameter} instance for the given name
	 *
	 * @param name the name of the {@link Parameter} instance to fetch
	 * @return a {@link Parameter} for the given name or null if not found.
	 */
	public Parameter forName(final String name) {
		Parameter value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<Parameter> query = em.createNamedQuery("Parameter.findByName", Parameter.class);
			query.setParameter("name", name);
			final List<Parameter> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}