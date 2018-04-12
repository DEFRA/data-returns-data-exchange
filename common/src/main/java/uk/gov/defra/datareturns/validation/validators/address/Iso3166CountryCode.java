package uk.gov.defra.datareturns.validation.validators.address;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validate that the annotated value is a valid ISO 3166-2 alpha2 code.
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Iso3166Validator.class)
@Documented
public @interface Iso3166CountryCode {
    /**
     * Default constraint violation template
     *
     * @return the constraint violation template
     */
    String message() default "INVALID_ISO3166-2_COUNTRY_CODE";

    /**
     * Validation groups
     *
     * @return the groups that this validator is associated with
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload
     *
     * @return the Payload
     */
    Class<? extends Payload>[] payload() default {};
}
