package uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist;

public interface ControlledListAuditor {

	/**
	 * Check if a value is valid when checked against a list of controlled values
	 *
	 * @param value the value to be checked
	 * @return true if the value has been checked and is valid, false otherwise.
	 */
	boolean isValid(Object value);
}
