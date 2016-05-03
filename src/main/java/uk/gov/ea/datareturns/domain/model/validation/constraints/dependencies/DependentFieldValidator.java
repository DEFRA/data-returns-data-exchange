/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sam
 *
 */
public class DependentFieldValidator implements ConstraintValidator<DependentField, Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DependentFieldValidator.class);

	private DependentFieldAuditor auditor;

	private String primaryFieldGetterName;

	private String dependentFieldGetterName;

	@Override
	public void initialize(final DependentField constraintAnnotation) {
		System.out.println("initialising DependentFieldValidator");
		this.primaryFieldGetterName = constraintAnnotation.primaryFieldGetter();
		this.dependentFieldGetterName = constraintAnnotation.dependentFieldGetter();
		try {
			final Class<? extends DependentFieldAuditor> providerType = constraintAnnotation.auditor();
			this.auditor = providerType.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			LOGGER.error("Failed to set up dependent field validator", e);

		}
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final Method primaryFieldGetter = value.getClass().getDeclaredMethod(this.primaryFieldGetterName);
			final Method dependentFieldGetter = value.getClass().getDeclaredMethod(this.dependentFieldGetterName);

			final Object primaryFieldValue = primaryFieldGetter.invoke(value);
			final Object dependentFieldValue = dependentFieldGetter.invoke(value);

			return this.auditor.isValid(primaryFieldValue, dependentFieldValue);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
		return true;
	}
}
