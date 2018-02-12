package uk.gov.defra.datareturns.validation.constraints.annotations;

import uk.gov.defra.datareturns.validation.constraints.validators.SiteMatchesUniqueIdentifierValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Graham Willis
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SiteMatchesUniqueIdentifierValidator.class)
@Documented
public @interface SiteMatchesUniqueIdentifier {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
