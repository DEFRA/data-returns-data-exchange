/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.UniqueIdentifierDao;

/**
 * Controlled list value for unique identifiers
 *
 * @author Sam Gardner-Dell
 */
public class UniqueIdentifierAuditor implements ControlledListAuditor {
	/**
	 *
	 */
	public UniqueIdentifierAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return UniqueIdentifierDao.getInstance().identfierExists(Objects.toString(value, ""));
	}
}
