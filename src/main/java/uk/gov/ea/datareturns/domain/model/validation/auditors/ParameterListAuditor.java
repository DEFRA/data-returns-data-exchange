/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.ParameterDao;

/**
 *
 * @author Sam Gardner-Dell
 */
public class ParameterListAuditor implements ControlledListAuditor {
	/**
	 *
	 */
	public ParameterListAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return ParameterDao.getInstance().forName(String.valueOf(value)) != null;
	}

}
