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
	String message() default "{uk.gov.ea.datareturns.domain.model.validation.dependentfield.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String primaryFieldGetter();

	String dependentFieldGetter();

	//	Class<? extends ControlledListAuditor> value();
}