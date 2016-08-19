package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import java.lang.annotation.*;

/**
 * Created by graham on 19/08/16.
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Modifier {
    Class<? extends EntityModifier> modifier() default NullModifier.class;
}
