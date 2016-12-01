package uk.gov.ea.datareturns.domain.model.validation.constraints.factory;


import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl.ParameterHierarchyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ParameterHierarchyValidator.class)
@Documented

public @interface HierarchyValidator {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    Class<?> value() default Object.class;
}