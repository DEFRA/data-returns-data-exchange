package uk.gov.ea.datareturns.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

/**
 * Created by graham on 08/03/17.
 */
public class EntityCreatedEvent<E extends ControlledListEntity> implements ResolvableTypeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCreatedEvent.class);

    private E entity;

    public EntityCreatedEvent(E entity) {
        this.entity = entity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(entity));
    }
}
