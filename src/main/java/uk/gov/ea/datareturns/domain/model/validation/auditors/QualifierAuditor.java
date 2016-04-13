/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.QualifierDao;
import uk.gov.ea.datareturns.jpa.dao.ReferencePeriodDao;

/**
 * Controlled list auditor for qualifiers
 *
 * @author Sam Gardner-Dell
 */
public class QualifierAuditor implements ControlledListAuditor {
	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return QualifierDao.getInstance().forName(Objects.toString(value, "")) != null;
	}
}