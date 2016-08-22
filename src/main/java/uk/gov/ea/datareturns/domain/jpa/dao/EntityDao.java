/**
 *
 */
package uk.gov.ea.datareturns.domain.jpa.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Base class for JPA based DAO classes
 *
 * @author Graham Willis
 */
public abstract class EntityDao<E extends ControlledListEntity> {
	protected static final Logger LOGGER = LoggerFactory.getLogger(EntityDao.class);

	@PersistenceContext
	protected EntityManager entityManager;

	protected final Class<E> entityClass;

	protected volatile Map<String, E> cacheByName = null;
	protected volatile Map<String, E> cacheByNameKey = null;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    protected EntityDao(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

    /**
     * Get an entity of type <E> by is unique identifier
     *
     * @param id The entity identifier
     * @return E
     */
    public E getById(final long id) {
		return entityManager.find(entityClass, id);
	}

    /**
     *
     * @param name case sensitive name search term - will also get any aliases
     * @return The entity E or null
     */
    public E getByName(String name) {
        return getCache().get(name);
    }

    /**
     * Check for the name ignoring cases and multiple spaces
     * @param name
     * @return
     */
    public boolean nameExistsRelaxed(final String name) {
        return getKeyCache().get(ControlledListEntity.getKeyFromRelaxedName(name)) != null;
    }

    /**
     * Determine if a method or standard with the given name exists
     *
     * @param name the method or standard name to check
     * @return true if the name exists, false otherwise
     */
    public boolean nameExists(final String name) {
        return getCache().get(name) != null;
    }

    /**
     * Convert the relaxed name into the exact one held in the list
     * @param name
     * @return
     */
    public String getStandardizedName(final String name) {
        if (name == null) {
            return null;
        } else {
            E e = getKeyCache().get(ControlledListEntity.getKeyFromRelaxedName(name));
            if (e != null) {
                return e.getName();
            } else {
                return null;
            }
        }
    }

    /**
     * List all entities of type E. This list invocation includes any aliases as primaries
     *
     * @return List<E>
     */
    public List<E> list() {
		return getCache()
				.entrySet()
				.stream()
				.map(Map.Entry::getValue)
				.sorted(comparing(E::getId))
				.collect(Collectors.toList());
	}

    /**
     * List all entities of type <E> which satisfy a predicate
     *
     * @param predicate The predicate
     * @return List<E>
     */
    public List<E> list(Predicate<E> predicate) {
		return getCache()
				.entrySet()
				.stream()
				.map(Map.Entry::getValue)
				.filter(predicate)
				.sorted(comparing(E::getId))
				.collect(Collectors.toList());
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
                            return readMethod.invoke(e).toString().toLowerCase().replaceAll("\\s+","")
									.contains(contains.toLowerCase().replaceAll("\\s+",""));
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

	/**
	 * Builds the cache if necessary and returns built cache. The cache will include any aliases
	 * as primaries so we can check for the existence of aliases using this (super) class method
	 */
	protected Map<String, E> getCache() {
		if (cacheByName == null) {
			synchronized(this) {
				if (cacheByName == null) {
					LOGGER.info("Build name cache of: " + entityClass.getSimpleName());
					CriteriaBuilder cb = entityManager.getCriteriaBuilder();
					CriteriaQuery<E> q = cb.createQuery(entityClass);
					Root<E> c = q.from(entityClass);
					q.select(c);
					TypedQuery<E> query = entityManager.createQuery(q);
					List<E> results = query.getResultList();
					cacheByName = results
							.stream()
							.collect(Collectors.toMap(ControlledListEntity::getName, k -> k));
				}
			}
		}
		return cacheByName;
	}

    /**
     * Add a new entity of type <E>
     *
     */
    @Transactional
    public void add(E entity) {
        entityManager.persist(entity);
        entityManager.flush();
        LOGGER.info("Added: " + entityClass.getSimpleName() + "id: " + entity.getId() + " " + entity.getName());
        clearCache();
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
        entityManager.remove(entity);
        LOGGER.info("Deleted: " + entityClass.getSimpleName() + " ID: " + id);
        clearCache();
    }

    /**
     * Builds the uppercase cache if necessary and returns built cache. The cache will include any aliases
     * as primaries so we can check for the existence of aliases using this (super) class method
     */
	protected Map<String, E> getKeyCache() {
		if (cacheByNameKey == null) {
            Map<String, E> localCache = getCache();
			synchronized(this) {
				if (cacheByNameKey == null) {
                    LOGGER.info("Build key cache of: " + entityClass.getSimpleName());
					cacheByNameKey = localCache.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toMap(e -> e.getKeyFromRelaxedName(), e -> e));
				}
			}
		}
		return cacheByNameKey;
	}

	/**
	 * Clear the cache by name
	 */
	protected void clearCache() {
        if (cacheByNameKey != null) {
            synchronized (this) {
                if (cacheByNameKey != null) {
                    cacheByNameKey = null;
                    LOGGER.info("Clear name cache: " + entityClass.getSimpleName());
                }
            }
        }
        if (cacheByName != null) {
            synchronized (this) {
                if (cacheByName != null) {
                    cacheByName = null;
                    LOGGER.info("Clear key cache: " + entityClass.getSimpleName());
                }
            }
        }
	}

	/**
	 * Get the class of the entity being operated on
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}
}
