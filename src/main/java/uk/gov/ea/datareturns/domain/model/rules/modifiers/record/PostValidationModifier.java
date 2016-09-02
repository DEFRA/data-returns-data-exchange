package uk.gov.ea.datareturns.domain.model.rules.modifiers.record;

import java.lang.annotation.*;

/**
 * Graham Willis: 19/08/16.
 * Causes the field described in the @Parsed annotation
 * to be modified by the class specified in modifier
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PostValidationModifiers.class)
public @interface PostValidationModifier {
    Class<? extends RecordModifier> modifier();
}
