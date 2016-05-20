package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class UnitDao extends AbstractJpaDao {
	/**
	 *
	 */
	public UnitDao() {
		super();
	}

	/**
	 * Determine if a specific name exists in the units controlled list
	 *
	 * @param name the name to test
	 * @return true if the name exists, false otherwise.
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of unit names
	 *
	 * @return a {@link Set} of unit names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("Unit.findAllNames");
	}
}