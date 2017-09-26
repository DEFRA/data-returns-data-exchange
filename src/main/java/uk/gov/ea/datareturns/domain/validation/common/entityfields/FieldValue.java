package uk.gov.ea.datareturns.domain.validation.common.entityfields;

/**
 * Represents a single field value for a record
 *
 * @param <T>  the type parameter
 * @author Sam Gardner-Dell
 */
public interface FieldValue<T> {
    String getInputValue();
}