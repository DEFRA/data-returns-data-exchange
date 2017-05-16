package uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to enable hibernate validator based validation of records based on the record pattern
 *
 * @author Sam Gardner-Dell
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RecordValidatorFactory.class)
@Documented
public @interface ValidRecord {
    /**
     * Default constraint violation template
     * @return the constraint violation template
     */
    String message() default "{uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.record}";

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
     * The class of the record being validated - this will be the same as the class the annotations is defined on but must be passed
     * to this annotation as it is not possible to determine which class
     *
     * @return
     */
    Class<?> value() default Object.class;
}
