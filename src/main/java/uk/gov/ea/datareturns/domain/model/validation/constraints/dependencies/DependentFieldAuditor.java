package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

/**
 * Interface for auditors of dependent fields
 *
 * @author Sam Gardner-Dell
 */
public interface DependentFieldAuditor {
    /**
     * Determine if the dependentFieldValue is valid based on the value stored in the primaryFieldValue
     *
     * @param primaryFieldValue the primary field which dictates what is considered valid in the dependent field
     * @param dependentFieldValue the dependent field which will be validated
     * @return true if the dependent field is valid, false otherwise
     */
    boolean isValid(Object primaryFieldValue, Object dependentFieldValue);
}
