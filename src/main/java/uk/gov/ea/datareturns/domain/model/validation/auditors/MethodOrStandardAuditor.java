/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.MethodOrStandardDao;

/**
 * Controlled list auditor for Method or Standards (Meth_Stand).
 *
 * @author Sam Gardner-Dell
 */
@Component
public class MethodOrStandardAuditor implements ControlledListAuditor {
	@Inject
	private MethodOrStandardDao dao;
	
	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return dao.nameExists(Objects.toString(value, ""));
	}
}