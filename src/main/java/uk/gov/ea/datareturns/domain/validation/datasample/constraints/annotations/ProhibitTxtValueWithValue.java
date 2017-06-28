package uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations;

import uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators.ProhibitTxtValueWithValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by graham on 11/05/17.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProhibitTxtValueWithValueValidator.class)
@Documented
public @interface ProhibitTxtValueWithValue {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
