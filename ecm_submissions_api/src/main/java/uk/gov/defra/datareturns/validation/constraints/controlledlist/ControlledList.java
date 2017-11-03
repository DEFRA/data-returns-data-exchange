package uk.gov.defra.datareturns.validation.constraints.controlledlist;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable hibernate validator based validation of values that
 * should be constrained to a controlled list of values.
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ControlledListValidator.class)
@Documented
public @interface ControlledList {
    /**
     * Default constraint violation template
     *
     * @return the constraint violation template
     */
    String message() default "{uk.gov.ea.datareturns.domain.validation.model.validation.controlledlist.message}";

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

    /**
     * The master data entity or entities names which should be used to lookup allowed values in order to discover if the entry is valid
     *
     * @return the entity names to use
     */
    String[] entities();
}
