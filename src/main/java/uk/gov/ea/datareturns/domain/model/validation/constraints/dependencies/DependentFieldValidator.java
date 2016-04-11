/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author sam
 *
 */
public class DependentFieldValidator implements ConstraintValidator<DependentField, Object> {
	private String primaryFieldGetterName;
	private String dependentFieldGetterName;

	@Override
	public void initialize(final DependentField constraintAnnotation) {
		this.primaryFieldGetterName = constraintAnnotation.primaryFieldGetter();
		this.dependentFieldGetterName = constraintAnnotation.dependentFieldGetter();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final Method primaryFieldGetter = value.getClass().getDeclaredMethod(this.primaryFieldGetterName);
			final Method dependentFieldGetter = value.getClass().getDeclaredMethod(this.dependentFieldGetterName);

			final Object primaryFieldValue = primaryFieldGetter.invoke(value);
			final Object dependentFieldValue = dependentFieldGetter.invoke(value);
			//	
			//			System.out.println("Doing dependentfield validation, primary = " + primaryFieldValue + " dependent=" + dependentFieldValue);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
		return true;
	}
}
