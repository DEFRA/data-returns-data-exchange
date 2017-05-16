package uk.gov.ea.datareturns.domain.validation.model.validation.constraints.factory;


import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.factory.impl.ParameterHierarchyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The hierarchy Validation has its own interface to allow it to
 * have a different group to the other record level validators.
 * @author Graham Willis
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ParameterHierarchyValidator.class)
@Documented
public @interface HierarchyValidator {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    Class<?> value() default Object.class;
}