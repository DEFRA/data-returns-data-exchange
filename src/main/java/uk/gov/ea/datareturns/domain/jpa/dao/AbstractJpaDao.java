/**
 *
 */
package uk.gov.ea.datareturns.domain.jpa.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.PersistedEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class for JPA based DAO classes
 *
 * @author Sam Gardner-Dell, Graham Willis
 */
@Repository
public abstract class AbstractJpaDao<E extends PersistedEntity> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaDao.class);

	@PersistenceContext
	private EntityManager entityManager;

    private final Class<E> entityClass;

	private Map<String, E> cacheByName = null;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    protected AbstractJpaDao(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

    /**
     * Get an entity of type <E> by is unique identifier
     *
     * @param id The entity identifier
     * @return E
     */
    private E getById(final long id) {
		return entityManager.find(entityClass, id);
	}

    /**
     * List all entities of type E
     *
     * @return List<E>
     */
    public List<E> list() {
		buildCacheIfNeeded();
		return cacheByName.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

    /**
     * List all entities of type <E> which satisfy a predicate
     *
     * @param predicate The predicate
     * @return List<E>
     */
    public List<E> list(Predicate<E> predicate) {
		buildCacheIfNeeded();
		return cacheByName.entrySet().stream().map(Map.Entry::getValue).filter(predicate).collect(Collectors.toList());
	}

    /**
     * List the entities of type <E> filtered such that the field contains the search term
     *
     * @param field The name of the field used for filtering
     * @param contains The search term
     * @return A filtered list
     */
    public List<E> list(String field, String contains) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(entityClass).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && pd.getName().toLowerCase().equals(field.toLowerCase())) {
                    final Method readMethod = pd.getReadMethod();
                    Predicate<E> builtPredicate = e -> {
                        try {
                            return readMethod.invoke(e).toString().toLowerCase().contains(contains);
                        } catch (IllegalAccessException e1) {
                            return true;
                        } catch (InvocationTargetException e1) {
                            return true;
                        }
                    };
                    return list(builtPredicate);
                }
            }
        } catch (IntrospectionException e) {
            return list();
        }
        return list();
    }

	private void buildCacheIfNeeded() {
		if (cacheByName == null) {
			LOGGER.info("Build cache of: " + entityClass.getName());
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<E> q = cb.createQuery(entityClass);
			Root<E> c = q.from(entityClass);
			q.select(c);
			TypedQuery<E> query = entityManager.createQuery(q);
			List<E> results = query.getResultList();
			cacheByName = results.stream().collect(Collectors.toMap(PersistedEntity::getName, k -> k));
		}
	}

	/**
	 * Determine if a method or standard with the given name exists
	 *
	 * @param name the method or standard name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of method or standard names
	 *
	 * @return a {@link Set} of method or standard names
	 */
    private Set<String> findNames() {
		buildCacheIfNeeded();
		return cacheByName.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
	}

    /**
     * Add a new entity of type <E>
     *
     * @param entity The entity to add
     */
	@Transactional
	public void add(E entity) {
		entityManager.persist(entity);
		cacheByName.put(entity.getName(), entity);
		LOGGER.info("ADDED: " + entity);
	}

    /**
     * Remove an entity of type <E>
     *
     * @param id The entity identifier
     * @throws IllegalArgumentException
     */
	@Transactional
	public void removeById(long id) throws IllegalArgumentException {
		E entity = getById(id);
		cacheByName.remove(entity.getName());
		entityManager.remove(entity);
		LOGGER.info("DELETE: " + entityClass.getSimpleName() + " ID: " + id);
	}

}
