package uk.gov.defra.datareturns.validation.constraints.annotations;

import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.constraints.validators.PermitSiteRelationshipValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable hibernate validator based validation that a permit and site are correctly associated
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PermitSiteRelationshipValidator.class)
@Documented
public @interface ValidPermitSiteRelationship {
    /**
     * Default message template for violations
     *
     * @return the message template to use for violations
     */
    String message() default EcmErrorCodes.Conflict.UNIQUE_IDENTIFIER_SITE_CONFLICT;

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
