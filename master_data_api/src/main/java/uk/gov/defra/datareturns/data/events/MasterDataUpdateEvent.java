package uk.gov.defra.datareturns.data.events;

import org.springframework.context.ApplicationEvent;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

/**
 * The {@link MasterDataUpdateEvent} is fired whenever an entity extending {@link AbstractMasterDataEntity} is added, updated or deleted
 *
 * @param <E>
 * @author Sam Gardner-Dell
 */
public final class MasterDataUpdateEvent<E extends AbstractMasterDataEntity> extends ApplicationEvent {
    private final E instance;

    public MasterDataUpdateEvent(final E instance) {
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
