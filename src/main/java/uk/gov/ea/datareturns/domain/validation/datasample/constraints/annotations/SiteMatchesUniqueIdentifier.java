package uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations;

import uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators.SiteMatchesUniqueIdentifierValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Graham Willis
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SiteMatchesUniqueIdentifierValidator.class)
@Documented
public @interface SiteMatchesUniqueIdentifier {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
