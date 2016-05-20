/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.jpa.dao.MonitoringPeriodDao;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

/**
 * Controlled list auditor for monitoring periods
 *
 * @author Sam Gardner-Dell
 */
@Component
public class MonitoringPeriodAuditor implements ControlledListAuditor {
	@Inject
	private MonitoringPeriodDao dao;

	/**
	 *
	 */
	public MonitoringPeriodAuditor() {

	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return this.dao.nameExists(Objects.toString(value, ""));
	}
}