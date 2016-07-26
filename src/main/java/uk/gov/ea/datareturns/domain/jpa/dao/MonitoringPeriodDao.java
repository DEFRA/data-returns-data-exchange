package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.MonitoringPeriod;

/**
 * DAO for monitoring periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class MonitoringPeriodDao extends AbstractJpaDao {

	public MonitoringPeriodDao() {
		super(MonitoringPeriod.class);
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