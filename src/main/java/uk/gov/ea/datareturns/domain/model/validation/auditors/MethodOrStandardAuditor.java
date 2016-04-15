/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.MethodOrStandardDao;

/**
 * Controlled list auditor for Method or Standards (Meth_Stand).
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandardAuditor implements ControlledListAuditor {
	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return MethodOrStandardDao.getInstance().nameExists(Objects.toString(value, ""));
	}
}