package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;

/**
 * @author Graham Willis
 * Used so to keep the hibernate entities as clean representations
 * of the persistence objects
 */
public interface AbstractMeasurementFactory<M extends AbstractMeasurement, D extends MeasurementDto> {
    M create(D dto);
}
