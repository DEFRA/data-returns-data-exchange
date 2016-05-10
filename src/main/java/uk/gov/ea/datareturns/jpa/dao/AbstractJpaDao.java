/**
 *
 */
package uk.gov.ea.datareturns.jpa.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author Sam Gardner-Dell
 *
 */
@Repository
public abstract class AbstractJpaDao {
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaDao.class);

	@Inject
	private EntityManager entityManager;

	// Cached lists
	private static final Map<String, Set<String>> CACHED_STRING_SETS = Collections.synchronizedMap(new HashMap<>());

	/**
	 *
	 */
	public AbstractJpaDao() {
	}

	/**
	 * Run a query that expects a List of Strings as a result
	 *
	 * @param namedQuery the named query to run (should result in a list of Strings being selected)
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
