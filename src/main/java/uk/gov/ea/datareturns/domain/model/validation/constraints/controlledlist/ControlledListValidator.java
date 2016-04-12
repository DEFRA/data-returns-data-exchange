/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * Allows validation against a controlled list of values
 *
 * @author Sam Gardner-Dell
 */
public class ControlledListValidator implements ConstraintValidator<ControlledList, Object> {
	/** ControlledListProvider instance to provide the list of values we must validate against */
	private ControlledListAuditor provider;
	private boolean required = true;

	@Override
	public void initialize(final ControlledList constraintAnnotation) {
		try {
			final Class<? extends ControlledListAuditor> providerType = constraintAnnotation.auditor();
			this.provider = providerType.newInstance();
			this.required = constraintAnnotation.required();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean valid = false;
		if (!this.required && StringUtils.isEmpty(Objects.toString(value, ""))) {
			// If field is not set to required and value is empty then this is valid.
			valid = true;
		} else {
			// Assume item is valid if there is no list to validate against.
			if (this.provider != null) {
				valid = this.provider.isValid(value);
			}
		}
		return valid;
	}
}
