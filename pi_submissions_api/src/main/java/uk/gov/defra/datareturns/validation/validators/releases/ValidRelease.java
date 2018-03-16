package uk.gov.defra.datareturns.validation.validators.releases;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validate a release object
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseValidator.class)
@Documented
public @interface ValidRelease {
    /**
     * Default constraint violation template
     *
     * @return the constraint violation template
     */
    String message() default "Release invalid";

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
