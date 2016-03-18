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

import org.hibernate.validator.constraints.NotBlank;

/**
 * Annotation to enable hibernate validator based validation of monitoring dates
 * 
 * @author Sam Gardner-Dell
 */

@NotBlank(message = "{uk.gov.ea.datareturns.monitoringDate.missing}")
// Date can be yyyy-mm-dd or dd-mm-yyyy optionally followed by Thh:mm:ss (e.g. 2016-03-11T09:00:00)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReturnsDateValidator.class)
@Documented
public @interface ValidReturnsDate {
	String message() default "{uk.gov.ea.datareturns.monitoringDate.invalid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
