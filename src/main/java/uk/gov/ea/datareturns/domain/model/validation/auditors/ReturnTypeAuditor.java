/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.ReturnTypeDao;

/**
 * Controlled list auditor for Return Types.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnTypeAuditor implements ControlledListAuditor {
	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return ReturnTypeDao.getInstance().forName(Objects.toString(value, "")) != null;
	}
}