package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import uk.gov.ea.datareturns.web.resource.ObservationSerializationBean;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;

/**
 * @author Graham Willis
 * Used so to keep the hibernate entities as clean representations
 * of the persistence objects
 */
public interface AbstractObservationFactory<M extends AbstractObservation, D extends ObservationSerializationBean> {
    /**
     * Create the persistent entity from the DTO - data transfer object
     * @param dto
     * @return
     */
    M create(D dto);
}
