package uk.gov.ea.datareturns.domain.model.rules.modifiers.record;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by graham on 01/09/16.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PostValidationModifiers {
    PostValidationModifier[] value();
}
