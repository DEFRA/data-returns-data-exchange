package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurement;

/**
 * @author Graham Willis
 */
public class MeasurementDao<M extends AbstractMeasurement> extends AbstractUserDataDao<M> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     */
    public MeasurementDao(Class<M> cls) {
        super(cls);
    }
}
