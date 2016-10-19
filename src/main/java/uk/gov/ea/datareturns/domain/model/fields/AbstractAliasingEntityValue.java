package uk.gov.ea.datareturns.domain.model.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;

/**
 * The {@link AbstractAliasingEntityValue} provides common transformation functionality for entities which are sublasses of
 * the {@link AliasingEntity} class.
 *
 * @param <R>  Parmeterized type for the record the entity belongs to
 * @param <E>  Parmeterized type for the entity
 */
public abstract class AbstractAliasingEntityValue<R, E extends AliasingEntity> extends AbstractEntityValue<R, E> {

    /**
     * Instantiates a new Abstract aliasing entity value.
     *
     * @param daoCls the dao cls
     * @param inputValue the input value
     */
    public AbstractAliasingEntityValue(Class<? extends EntityDao<E>> daoCls, String inputValue) {
        super(daoCls, inputValue);
    }

    /**
     * Provide standard transformation functionality based on the {@link AliasingEntity} aliasing functionality
     *
     * @param record the record to which the entity belongs
     * @return the transformed output value of this entity for use in the downstream system.
     */
    @Override public String transform(R record) {
        return super.getEntity() != null ? super.getEntity().getPrimaryName() : null;
    }
}