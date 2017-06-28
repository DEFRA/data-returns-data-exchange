package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations;

import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators.RequireValueOrTextValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by graham on 11/05/17.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireValueOrTextValueValidator.class)
@Documented
public @interface RequireValueOrTxtValue {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
