package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

public interface DependentFieldAuditor {

	boolean isValid(Object primaryFieldValue, Object dependentFieldValue);
}
