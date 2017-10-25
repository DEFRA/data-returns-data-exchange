package uk.gov.ea.datareturns.domain.jpa.repositories.events;

import org.springframework.context.ApplicationEvent;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.AbstractMasterDataEntity;

/**
 * The {@link MasterDataUpdateEvent} is fired whenever an entity extending {@link AbstractMasterDataEntity} is added, updated or deleted
 * @param <E>
 * @author Sam Gardner-Dell
 */
public class MasterDataUpdateEvent<E extends AbstractMasterDataEntity> extends ApplicationEvent {
    private E instance;

    public MasterDataUpdateEvent(E instance) {
        super(instance);
        this.instance = instance;
    }

    public Class<? extends MasterDataEntity> getEntityClass() {
        return instance.getClass();
    }

    public E getInstance() {
        return instance;
    }
}
