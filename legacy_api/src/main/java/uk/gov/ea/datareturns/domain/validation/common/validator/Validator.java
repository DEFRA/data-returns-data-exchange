package uk.gov.ea.datareturns.domain.validation.common.validator;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;

import java.util.Set;

/**
 * @author Graham Willis
 */
public interface Validator<V extends AbstractValidationObject> {

    /**
     * Validate a measurement object recording using the javax validation standard
     */
    Set<ValidationError> validateValidationObject(V validationObject);
}