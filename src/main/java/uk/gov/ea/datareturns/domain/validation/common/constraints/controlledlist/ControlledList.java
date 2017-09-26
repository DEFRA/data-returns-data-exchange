package uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.AbstractMasterDataEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to enable hibernate validator based validation of values that
 * should be constrained to a controlled list of values.
 *
 * @author Sam Gardner-Dell
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ControlledListValidator.class)
@Documented
public @interface ControlledList {
    /**
     * Default constraint violation template
     * @return the constraint violation template
     */
    String message() default "{uk.gov.ea.datareturns.domain.validation.model.validation.controlledlist.message}";

    /**
     * Validation groups
     * @return the groups that this validator is associated with
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload
     * @return the Payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The JPA entity class which should be used to lookup allowed values in order to discover if the entry is valid
     *
     * @return the {@link Class} definition for the entity class
     */
    Class<? extends AbstractMasterDataEntity>[] entities();
}
