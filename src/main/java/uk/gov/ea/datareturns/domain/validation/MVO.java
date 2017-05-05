package uk.gov.ea.datareturns.domain.validation;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;

/**
 * @author Graham Willis
 * Measurement validation object
 *
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicabe for a given measurement
 * type
 */
public abstract class MVO<D extends MeasurementDto> {
    protected final D dto;

    public MVO(D dto) {
        this.dto = dto;
    }
}
