/**
 *
 */
package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.PersistedEntity;

/**
 * Base class for JPA based DAO classes
 *
 * @author Sam Gardner-Dell
 */
@Repository
public abstract class AbstractJpaDao<E extends PersistedEntity> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaDao.class);

	@Inject
	@PersistenceContext
	private EntityManager entityManager;

    private Class<E> entityClass;

	// Cached lists
	private static final Map<String, Set<String>> CACHED_STRING_SETS = Collections.synchronizedMap(new HashMap<>());

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public AbstractJpaDao(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	public E getById(final long id) {
		return entityManager.find(entityClass, id);
	}

	public List<E> list() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> q = cb.createQuery(entityClass);
		Root<E> c = q.from(entityClass);
		q.select(c);
		TypedQuery<E> query = entityManager.createQuery(q);
		return query.getResultList();
	}

	/**
	 * Run a query that expects a List of Strings as a result
	 *
	 * @param namedQuery the named query to run (should result in a list of Strings being selected)
	 * @param target the collection instance to populate with results
	 * @return a {@link List} of Strings containing the results
	 */
	protected <T extends Collection<String>> T stringColumnQuery(final String namedQuery, final T target) {
		try {
			final TypedQuery<String> query = this.entityManager.createNamedQuery(namedQuery, String.class);
			query.setHint(QueryHints.CACHEABLE, "true");
			target.addAll(query.getResultList());
			return target;
		} finally {
			this.entityManager.close();
		}
	}

	/**
	 * Run a query for data in a particular column
	 *
	 * @param namedQuery the named query to execute
	 * @return a {@link Set} of Strings for the data retrieved from the query
	 */
	protected Set<String> cachedColumnQuery(final String namedQuery) {
		Set<String> cachedSet = CACHED_STRING_SETS.get(namedQuery);
		if (cachedSet == null) {
			synchronized (CACHED_STRING_SETS) {
				cachedSet = CACHED_STRING_SETS.get(namedQuery);
				if (cachedSet == null) {
					cachedSet = stringColumnQuery(namedQuery, new HashSet<>());
				}
				CACHED_STRING_SETS.put(namedQuery, cachedSet);
			}
		}
		return Collections.unmodifiableSet(cachedSet);
	}
}
