/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation to enable hibernate validator based validation of values that
 * should be constrained to a controlled list of values.
 *
 * @author Sam Gardner-Dell
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ControlledListValidator.class)
@Documented
public @interface ControlledList {
	String message() default "{uk.gov.ea.datareturns.domain.model.validation.controlledlist.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends ControlledListAuditor> auditor();
	
	boolean required() default true;
}
