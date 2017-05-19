package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;

import java.util.Set;

/**
 * @author Graham Willis
 */
public interface ObservationValidator<V extends Mvo> {

    /**
     * Validate a measurement object recording using the javax validation standard
     */
    Set<ValidationError> validateObservation(V observation);
}