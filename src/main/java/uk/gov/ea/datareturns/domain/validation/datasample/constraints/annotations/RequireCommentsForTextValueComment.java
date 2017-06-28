package uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations;

/**
 * Created by graham on 28/06/17.
 */

import uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators.RequireCommentsForTextValueCommentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireCommentsForTextValueCommentValidator.class)
@Documented
public @interface RequireCommentsForTextValueComment {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
