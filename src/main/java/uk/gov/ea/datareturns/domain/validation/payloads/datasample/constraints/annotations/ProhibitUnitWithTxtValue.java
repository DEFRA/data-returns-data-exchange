package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations;

import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators.ProhibitUnitForTxtValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by graham on 28/06/17.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProhibitUnitForTxtValueValidator.class)
@Documented
public @interface ProhibitUnitWithTxtValue {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
