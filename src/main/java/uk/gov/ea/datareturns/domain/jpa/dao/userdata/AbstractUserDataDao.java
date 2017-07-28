package uk.gov.ea.datareturns.domain.jpa.dao.userdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Graham Willis. Define the data access objects for the user data entities
 *
 * (These are distinct from the master data as they support a different set of operations
 * which are much more 'read-write' in nature.)
 */
public abstract class AbstractUserDataDao<E extends Userdata>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserDataDao.class);

    private final Class<E> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    protected AbstractUserDataDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public E persist(E entity) {
        entityManager.persist(entity);
        return entity;
    }

    public void remove(long id) {
        E entity = entityManager.find(entityClass, id);
        entityManager.remove(entity);
    }

    public void merge(E entity) {
        entityManager.merge(entity);
    }
}
