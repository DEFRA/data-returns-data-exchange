package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;

/**
 * @author Graham Willis
 */
public class PayloadEntityDao extends AbstractUserDataDao<AbstractPayloadEntity> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public PayloadEntityDao() {
        super(AbstractPayloadEntity.class);
    }
}
