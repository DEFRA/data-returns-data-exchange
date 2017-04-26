package uk.gov.ea.datareturns.domain.jpa.dao.userdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Graham Willis. Define the data access objects for the user data entities
 *
 * (These are distinct from the master data as they support a different set of operations
 * which are much more 'read-write' in nature.)
 */
public abstract class AbstractUserDataDao<E extends Userdata>  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserDataDao.class);

    private final Class<E> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public AbstractUserDataDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public E add(E entity) {
        entityManager.persist(entity);
        LOGGER.info("Adding: " + entityClass.getSimpleName() + ": " + entity.toString());
        return entity;
    }

    public abstract E getByIdentifier(String identifier);

    public void remove(String identifier) {
        E entity = getByIdentifier(identifier);
        LOGGER.info("Removing: " + entityClass.getSimpleName() + ": " + entity.toString());
        entityManager.remove(entity);
    }

}
