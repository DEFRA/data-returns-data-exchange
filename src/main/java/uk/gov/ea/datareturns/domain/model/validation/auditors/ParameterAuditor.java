/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;
import uk.gov.ea.datareturns.jpa.dao.ParameterDao;

/**
 * Controlled list auditor for parameters
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ParameterAuditor implements ControlledListAuditor {
	@Inject
	private ParameterDao dao;
	
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
		return dao.nameExists(Objects.toString(value, ""));
	}

}
