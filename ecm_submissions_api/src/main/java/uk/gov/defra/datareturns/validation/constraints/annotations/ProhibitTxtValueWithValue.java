package uk.gov.defra.datareturns.validation.constraints.annotations;

import uk.gov.defra.datareturns.validation.constraints.validators.ProhibitTxtValueWithValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by graham on 11/05/17.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProhibitTxtValueWithValueValidator.class)
@Documented
public @interface ProhibitTxtValueWithValue {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
