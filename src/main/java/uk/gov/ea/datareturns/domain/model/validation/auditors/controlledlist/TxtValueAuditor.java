/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.jpa.dao.QualifierDao;
import uk.gov.ea.datareturns.domain.model.rules.BooleanValue;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

/**
 * Controlled list auditor for the Txt_Value field.  Allows boolean values and qualifiers from the controlled list.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class TxtValueAuditor implements ControlledListAuditor {
	@Inject
	private QualifierDao dao;

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		return BooleanValue.from(value) != null || this.dao.nameExists(Objects.toString(value, ""));
	}
}