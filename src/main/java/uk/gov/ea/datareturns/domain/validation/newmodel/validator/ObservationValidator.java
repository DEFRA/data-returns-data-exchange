package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import uk.gov.ea.datareturns.domain.validation.newmodel.validator.result.ValidationResult;

/**
 * @author Graham Willis
 */
public interface ObservationValidator<V extends Mvo> {

    /**
     * Validate a measurement object recording using the javax validation standard
     */
    ValidationResult validateMeasurement(V measurement);
}