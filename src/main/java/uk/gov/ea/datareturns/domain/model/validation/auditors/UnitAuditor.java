/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.UnitDao;

/**
 * Controlled list auditor for unit values
 * 
 * @author Sam Gardner-Dell
 */
public class UnitAuditor implements ControlledListAuditor {
	/**
	 *
	 */
	public UnitAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return UnitDao.getInstance().forName(Objects.toString(value, "")) != null;
	}
}
