/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.ParameterDao;

/**
 * Controlled list auditor for parameters
 *
 * @author Sam Gardner-Dell
 */
public class ParameterAuditor implements ControlledListAuditor {
	/**
	 *
	 */
	public ParameterAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
//		return ParameterDao.getInstance().forName(Objects.toString(value, "")) != null;
		return ParameterDao.getInstance().nameExists(Objects.toString(value, ""));
	}

}
