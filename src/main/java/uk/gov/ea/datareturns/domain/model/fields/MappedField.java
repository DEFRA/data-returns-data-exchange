package uk.gov.ea.datareturns.domain.model.fields;

import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;

import java.lang.annotation.*;

/**
 * The {@link MappedField} annotation can be used to specify how Javabean fields map to fields in a file (such as CSV headings)
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappedField {
    /**
     * Value field definition.
     *
     * @return the field definition
     */
    FieldDefinition value();

    /**
     * Should this field be trimmed when reading/writing data
     * @return true if the data should be trimmed, false otherwise
     */
    boolean trim() default true;

    /**
     * Should special characters within this field be normalized based on the
     * {@link uk.gov.ea.datareturns.util.TextUtils.CharacterSubstitution} rules?
     * @return true if the data should be normalized, false otherwise
     */
    boolean normalize() default true;
}
