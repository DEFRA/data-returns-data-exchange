package uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

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
    /**
     * Default constraint violation template
     * @return the constraint violation template
     */
    String message() default "{uk.gov.ea.datareturns.domain.validation.model.validation.controlledlist.message}";

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
     * The controlled list auditor class responsible for auditing values in this field
     *
     * @return the {@link Class} definition for the auditor to use
     */
    Class<? extends ControlledListAuditor> auditor();
}
