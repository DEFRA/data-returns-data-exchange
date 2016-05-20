/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

/**
 * Controlled list value for unique identifiers
 *
 * @author Sam Gardner-Dell
 */
@Component
public class UniqueIdentifierAuditor implements ControlledListAuditor {
	@Inject
	private UniqueIdentifierDao dao;

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
		return this.dao.identfierExists(Objects.toString(value, ""));
	}
}
