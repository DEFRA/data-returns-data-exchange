package uk.gov.ea.datareturns.domain.model.fields;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;

/**
 * Represents a single field value for a record
 *
 * @param <R>  the type parameter
 * @param <T>  the type parameter
 * @author Sam Gardner-Dell
 */
public interface FieldValue<R, T> {
    /** Regular expression for fields which should only contain simple text (no special characters) */
    String REGEX_SIMPLE_TEXT = "^[a-zA-Z0-9 ]*$";

    /** Regular expression for fields which should only contain simple text and the EHS special delimiters / and - */
    String REGEX_EHS_DELIMITED_TEXT = "^[a-zA-Z0-9\\/\\- ]*$";

    /**
     * Gets input value.
     *
     * @return the original unchanged value exactly as supplied by the user
     */
    String getInputValue();

    /**
     * Gets value.
     *
     * @return the value that should be output to the downstream system for this field value
     */
    T getValue();

    /**
     * Transform output
     * @param record the record which this field belongs to
     * @return the appropriate output value for the downstream system
     */
    String transform(R record);

    /**
     * Gets field.
     *
     * @return the definition of the field this value belongs to
     */
    default FieldDefinition getField() {
        return FieldDefinition.forType(this.getClass());
    }

    /**
     * Check if a {@link FieldValue} is not empty based on the {@link FieldValue#getInputValue()}
     *
     * See {@link StringUtils#isNotEmpty(CharSequence)} for implementation detail.
     *
     * @param fv the {@link FieldValue} to be tested
     * @return true if not empty, false otherwise.
     */
    static boolean isNotEmpty(FieldValue fv) {
        return fv != null && StringUtils.isNotEmpty(fv.getInputValue());
    }

    /**
     * Check if a {@link FieldValue} is not blank based on the {@link FieldValue#getInputValue()}
     *
     * See {@link StringUtils#isNotBlank(CharSequence)} for implementation detail.
     *
     * @param fv the {@link FieldValue} to be tested
     * @return true if not blank, false otherwise.
     */
    static boolean isNotBlank(FieldValue fv) {
        return fv != null && StringUtils.isNotBlank(fv.getInputValue());
    }
}