package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation to enable hibernate validator based validation of interdependent values within a class
 *
 * @author Sam Gardner-Dell
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DependentFieldValidator.class)
@Documented
public @interface DependentField {
	/**
	 * Default constraint violation template
	 * @return the constraint violation template
	 */
	String message() default "{uk.gov.ea.datareturns.domain.model.validation.dependentfield.message}";

	/**
	 * Validation groups
	 * @return the groups that this validator is associated with
	 */
	Class<?>[] groups() default {};

	/**
	 * Validation payload
	 * @return the Payload
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * The name of the getter method for the primary field
	 *
	 * @return the name of the getter method for the primary field
	 */
	String primaryFieldGetter();

	/**
	 * The name of the getter method for the dependent field
	 *
	 * @return the name of the getter method for the dependent field
	 */
	String dependentFieldGetter();

	/**
	 * The {@link Class} definition for the auditor which will check for validity of the dependent field
	 * based on the value of the primary field
	 *
	 * @return the {@link Class} for the auditor
	 */
	Class<? extends DependentFieldAuditor> auditor();
}