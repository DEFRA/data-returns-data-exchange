package uk.gov.ea.datareturns.domain.jpa.dao;

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
}