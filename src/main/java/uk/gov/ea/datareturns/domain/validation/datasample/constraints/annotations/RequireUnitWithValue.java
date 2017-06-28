package uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations;

import uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators.RequireUnitWithValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by graham on 28/06/17.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireUnitWithValueValidator.class)
@Documented
public @interface RequireUnitWithValue {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
