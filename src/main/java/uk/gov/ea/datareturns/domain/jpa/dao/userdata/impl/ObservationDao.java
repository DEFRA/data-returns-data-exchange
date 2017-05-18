package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;

/**
 * @author Graham Willis
 */
public class ObservationDao<M extends AbstractObservation> extends AbstractUserDataDao<M> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     */
    public ObservationDao(Class<M> cls) {
        super(cls);
    }
}
