package uk.gov.defra.datareturns.validation.validators.nomenclature;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validate that the annotated value matches the nomenclature of a master data entity within one of the provided resource collection URIs
 *
 * @author Sam Gardner-Dell
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNomenclatureValidator.class)
@Documented
public @interface ValidNomenclature {
    /**
     * Default constraint violation template
     *
     * @return the constraint violation template
     */
    String message() default "Nomenclature not found";

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

    /**
     * The collection of URI's to check for existence of the id.
     *
     * @return the collection of URI's
     */
    String[] resourceCollectionUris() default {};
}
