package uk.gov.ea.datareturns.domain.validation.newmodel.entityfields;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a single field value for a record
 *
 * @param <T>  the type parameter
 * @author Sam Gardner-Dell
 */
public interface FieldValue<T> {

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