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
	public void initialize(DependentField constraintAnnotation) {
		primaryFieldGetterName = constraintAnnotation.primaryFieldGetter();
		dependentFieldGetterName = constraintAnnotation.dependentFieldGetter();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			Method primaryFieldGetter = value.getClass().getDeclaredMethod(primaryFieldGetterName);
			Method dependentFieldGetter = value.getClass().getDeclaredMethod(dependentFieldGetterName);
			
			Object primaryFieldValue = primaryFieldGetter.invoke(value);
			Object dependentFieldValue = dependentFieldGetter.invoke(value);
//	
//			System.out.println("Doing dependentfield validation, primary = " + primaryFieldValue + " dependent=" + dependentFieldValue);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return true;
	}
}
