package uk.gov.defra.datareturns.validation.constraints.annotations;

/**
 * Created by graham on 28/06/17.
 */

import uk.gov.defra.datareturns.validation.constraints.validators.RequireCommentsForTextValueCommentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireCommentsForTextValueCommentValidator.class)
@Documented
public @interface RequireCommentsForTextValueComment {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
