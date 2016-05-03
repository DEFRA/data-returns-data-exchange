/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.UnitDao;

/**
 * Controlled list auditor for unit values
 *
 * @author Sam Gardner-Dell
 */
@Component
public class UnitAuditor implements ControlledListAuditor {
	@Inject
	private UnitDao dao;

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
		return this.dao.nameExists(Objects.toString(value, ""));
	}
}
