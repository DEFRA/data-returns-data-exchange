package uk.gov.ea.datareturns.jpa.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.MonitoringPeriod;

public class MonitoringPeriodDao extends AbstractJpaDao {
	private static final MonitoringPeriodDao INSTANCE = new MonitoringPeriodDao();

	public static MonitoringPeriodDao getInstance() {
		return INSTANCE;
	}

	/**
	 * Get a {@link MonitoringPeriod} instance for the given name
	 *
	 * @param name the name of the {@link MonitoringPeriod} instance to fetch
	 * @return a {@link MonitoringPeriod} for the given name or null if not found.
	 */
	public MonitoringPeriod forName(final String name) {
		MonitoringPeriod value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<MonitoringPeriod> query = em.createNamedQuery("MonitoringPeriod.findByName", MonitoringPeriod.class);
			query.setParameter("name", name);
			final List<MonitoringPeriod> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}