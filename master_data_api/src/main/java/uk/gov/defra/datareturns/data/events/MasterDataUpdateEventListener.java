package uk.gov.defra.datareturns.data.events;

import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.util.SpringApplicationContextProvider;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public final class MasterDataUpdateEventListener {
    @PostUpdate
    public void postUpdate(final AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }

    @PostPersist
    public void postPersist(final AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }

    @PostRemove
    public void postRemove(final AbstractMasterDataEntity abstractEntity) {
        SpringApplicationContextProvider.getApplicationContext().publishEvent(new MasterDataUpdateEvent<>(abstractEntity));
    }
}
