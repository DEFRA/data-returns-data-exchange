package uk.gov.ea.datareturns.domain.model.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.AliasingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import java.util.Optional;

/**
 * The {@link AbstractAliasingEntityValue} provides common transformation functionality for entities which are sublasses of
 * the {@link AliasingEntity} class.
 *
 * @param <R>  Parmeterized type for the record the entity belongs to
 * @param <E>  Parmeterized type for the entity
 */
public abstract class AbstractAliasingEntityValue<R, E extends AliasingEntity> extends AbstractEntityValue<AliasingEntityDao<E>, R, E> {

    /**
     * Instantiates a new Abstract aliasing entity value.
     *
     * @param inputValue the input value
     */
    public AbstractAliasingEntityValue(String inputValue) {
        super(inputValue);
    }

    protected E findEntity(String inputValue) {
        return getDao().getByNameOrAlias(Key.relaxed(inputValue));
    }

    /**
     * Provide standard transformation functionality based on the {@link AliasingEntity} aliasing functionality
     *
     * @param record the record to which the entity belongs
     * @return the transformed output value of this entity for use in the downstream system.
     */
    @Override public String transform(R record) {
        String entityName = Optional.ofNullable(super.getEntity()).map(AliasingEntity::getName).orElse(null);
        // Use an explicit lookup as we are already using an entity
        return Optional.ofNullable(getDao().getPreferred(Key.explicit(entityName))).map(AliasingEntity::getName).orElse(null);
    }
}