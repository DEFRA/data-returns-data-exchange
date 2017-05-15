package uk.gov.ea.datareturns.domain.validation.model.fields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;

/**
 * The {@link AbstractEntityValue} class provides the base {@link FieldValue} implementation for fields which are backed by
 * a database entity
 *
 * @param <R>   Parmeterized type for the record the entity belongs to
 * @param <E>   Parmeterized type for the entity
 */
public abstract class AbstractEntityValue<D extends EntityDao<E>, E extends ControlledListEntity> implements FieldValue<E> {
    @JsonIgnore
    private E entity;

    /**
     * Instantiates a new Abstract entity value.
     *
     * @param inputValue the input value
     */
    public AbstractEntityValue(String inputValue) {
        this.entity = findEntity(inputValue);
    }

    protected abstract D getDao();

    /**
     * Find the entity for the given input value
     *
     * @param inputValue the term used to retrieve the entity from the data layer
     * @return the entity for the given term, or null if not found.
     */
    protected E findEntity(String inputValue) {
        return getDao().getByName(Key.relaxed(inputValue));
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
     * Helper function to provide null checking getting entity from field value from a static reference
     * @param fv The field Value
     * @param <E> The entity type
     * @return The entity
     */
    public static <E extends ControlledListEntity> E getEntity(AbstractEntityValue<?, E> fv) {
        return fv != null ? fv.getEntity() : null;
    }

}
