package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;

/**
 * @author Graham Willis
 */
public class PayloadEntityDao extends AbstractUserDataDao<AbstractPayloadEntity> {

    public PayloadEntityDao() {
        super(AbstractPayloadEntity.class);
    }
}
