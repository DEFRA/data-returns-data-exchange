package uk.gov.ea.datareturns.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class MonitoringPeriodDao extends AbstractJpaDao {
	/**
	 *
	 */
	public MonitoringPeriodDao() {
		super();
	}

	/**
	 * Determine if a monitoring period with the given name exists
	 *
	 * @param name the monitoring period name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of monitoring period names
	 *
	 * @return a {@link Set} of monitoring period names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("MonitoringPeriod.findAllNames");
	}
}