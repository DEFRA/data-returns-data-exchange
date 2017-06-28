package uk.gov.ea.datareturns.domain.validation.common.entityfields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.AliasingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

/**
 * The {@link AbstractAliasingEntityValue} provides common transformation functionality for entities which are sublasses of
 * the {@link AliasingEntity} class.
 *
 * @param <E>  Parmeterized type for the entity
 */
public abstract class AbstractAliasingEntityValue<E extends AliasingEntity> extends AbstractEntityValue<AliasingEntityDao<E>, E> {

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

}