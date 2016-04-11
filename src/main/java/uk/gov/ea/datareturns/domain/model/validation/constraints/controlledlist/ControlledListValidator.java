/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Allows validation against a controlled list of values
 *
 * @author Sam Gardner-Dell
 */
public class ControlledListValidator implements ConstraintValidator<ControlledList, Object> {
	/** ControlledListProvider instance to provide the list of values we must validate against */
	private ControlledListAuditor provider;

	@Override
	public void initialize(final ControlledList constraintAnnotation) {
		try {
			final Class<? extends ControlledListAuditor> providerType = constraintAnnotation.auditor();
			this.provider = providerType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean valid = false;
		// Assume item is valid if there is no list to validate against.
		if (this.provider != null) {
			valid = this.provider.isValid(value);
		}
		return valid;
	}
}
