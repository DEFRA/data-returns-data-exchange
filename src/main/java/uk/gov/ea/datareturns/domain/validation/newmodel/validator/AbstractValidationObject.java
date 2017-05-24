package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

/**
 * @author Graham Willis
 * Measurement validation object
 *
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicabe for a given measurement
 * type
 */
public abstract class AbstractValidationObject {
    protected final Payload payload;

    protected AbstractValidationObject(Payload payload) {
        this.payload = payload;
    }
}