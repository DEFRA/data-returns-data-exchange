package uk.gov.ea.datareturns.domain.jpa.dao;

import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * DAO for unique identifiers (EaId)
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class UniqueIdentifierDao {
	@PersistenceContext
	private EntityManager entityManager;

	// Cached lists
	private static final Map<String, Set<String>> CACHED_STRING_SETS = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Check if a specific identifier exists in the controlled list.
	 *
	 * @param identifier the identifier to check
	 * @return true if the identifier exists, false otherwise.
	 */
	public boolean identfierExists(final String identifier) {
		return findIdentifiers().contains(identifier);
	}

	/**
	 * Get a set of identifiers from the database
	 *
	 * @return a {@link UniqueIdentifier} for the given identifier or null if not found.
	 */
	public Set<String> findIdentifiers() {
		return cachedColumnQuery("UniqueIdentifier.findAllIdentifiers");
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
}
