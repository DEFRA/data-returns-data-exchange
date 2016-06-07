/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation to enable hibernate validator based validation of monitoring dates
 *
 * @author Sam Gardner-Dell
 */

// Date can be yyyy-mm-dd or dd-mm-yyyy optionally followed by Thh:mm:ss (e.g. 2016-03-11T09:00:00)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReturnsDateValidator.class)
@Documented
public @interface ValidReturnsDate {
	/**
	 * Default message template for violations
	 * @return the message template to use for violations
	 */
	String message() default "{DR9020-Incorrect}";

	/**
	 * The message template to use for missing dates
	 * @return the message template to use for violations
	 */
	String missingMessage() default "{DR9020-Missing}";

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
}
