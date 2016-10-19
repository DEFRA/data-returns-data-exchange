package uk.gov.ea.datareturns.domain.model.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
/**
 * The {@link AbstractEntityValue} class provides the base {@link FieldValue} implementation for fields which are backed by
 * a database entity
 *
 * @param <R>   Parmeterized type for the record the entity belongs to
 * @param <E>   Parmeterized type for the entity
 */
public abstract class AbstractEntityValue<R, E extends ControlledListEntity> implements FieldValue<R, E> {
    private E entity;

    /**
     * Instantiates a new Abstract entity value.
     *
     * @param daoCls the dao cls
     * @param inputValue the input value
     */
    public AbstractEntityValue(Class<? extends EntityDao<E>> daoCls, String inputValue) {
        this.entity = EntityDao.getDao(daoCls).getByNameRelaxed(inputValue);
    }

    /**
     * Retrieve the backing entity for this {@link FieldValue}.  May be null if no entity could be found for the input value
     *
     * @return the backing entity for this {@link FieldValue}.  May be null if no entity could be found for the input value
     */
    public final E getEntity() {
        return this.entity;
    }

    /**
     * Retrieve the backing entity for this {@link FieldValue}.  May be null if no entity could be found for the input value
     *
     * @return the backing entity for this {@link FieldValue}.  May be null if no entity could be found for the input value
     */
    @Override public E getValue() {
        return this.entity;
    }

    /**
     * Provide standard transformation functionality by using the standard name field from the referenced entity
     *
     * @param record the record to which the entity belongs
     * @return the transformed output value of this entity for use in the downstream system.
     */
    @Override public String transform(R record) {
        return this.entity != null ? this.entity.getName() : null;
    }
}
