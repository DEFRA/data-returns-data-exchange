package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;

/**
 * @author Graham Willis
 * Measurement validation object
 *
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicabe for a given measurement
 * type
 */
public abstract class Mvo<D extends MeasurementDto> {
    protected final D dto;

    protected Mvo(D dto) {
        this.dto = dto;
    }
}
