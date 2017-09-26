package uk.gov.ea.datareturns.domain.jpa.repositories.events;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.AbstractMasterDataEntity;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class MasterDataUpdateEventListener {
    @PostUpdate
    public void postUpdate(AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }

    @PostPersist
    public void postPersist(AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }

    @PostRemove
    public void postRemove(AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }
}
