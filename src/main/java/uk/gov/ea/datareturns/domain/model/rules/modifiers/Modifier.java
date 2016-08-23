package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import java.lang.annotation.*;

/**
 * Graham Willis: 19/08/16.
 * Causes the entity described in the @Parsed annotation
 * to be modified by the class specified in modifier
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Modifier {
    Class<? extends EntityModifier> modifier() default NullModifier.class;
}
