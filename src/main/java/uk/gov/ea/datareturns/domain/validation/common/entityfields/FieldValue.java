package uk.gov.ea.datareturns.domain.validation.common.entityfields;

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
}