package uk.gov.defra.datareturns.validation.constraints.annotations;

import uk.gov.defra.datareturns.validation.constraints.validators.ReturnPeriodValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable hibernate validator based validation of monitoring dates
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReturnPeriodValidator.class)
@Documented
public @interface ValidReturnPeriod {
    /**
     * Default message template for violations
     *
     * @return the message template to use for violations
     */
    String message() default "DR9070-Incorrect";

    /**
     * Validation groups
     *
     * @return the groups that this validator is associated with
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload
     *
     * @return the Payload
     */
    Class<? extends Payload>[] payload() default {};
}
