package uk.gov.ea.datareturns.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ReferencePeriodDao extends AbstractJpaDao {
	/**
	 *
	 */
	public ReferencePeriodDao() {
		super();
	}

	/**
	 * Determine if a reference period with the given name exists
	 *
	 * @param name the reference period name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of reference period names
	 *
	 * @return a {@link Set} of reference period names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("ReferencePeriod.findAllNames");
	}
}