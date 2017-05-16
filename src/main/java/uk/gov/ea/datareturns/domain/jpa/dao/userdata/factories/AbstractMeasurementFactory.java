package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurement;

/**
 * @author Graham Willis
 * Used so to keep the hibernate entities as clean representations
 * of the persistence objects
 */
public interface AbstractMeasurementFactory<M extends AbstractMeasurement, D extends MeasurementDto> {
    /**
     * Create the persistent entity from the DTO - data transfer object
     * @param dto
     * @return
     */
    M create(D dto);
}
