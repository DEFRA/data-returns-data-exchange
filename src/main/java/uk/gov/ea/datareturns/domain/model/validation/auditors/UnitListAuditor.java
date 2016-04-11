/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.UnitDao;

/**
 * @author sam
 *
 */
public class UnitListAuditor implements ControlledListAuditor {
	/**
	 *
	 */
	public UnitListAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return UnitDao.getInstance().forName(String.valueOf(value)) != null;
	}
}
