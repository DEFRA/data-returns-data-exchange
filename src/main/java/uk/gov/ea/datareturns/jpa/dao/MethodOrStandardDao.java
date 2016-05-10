package uk.gov.ea.datareturns.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class MethodOrStandardDao extends AbstractJpaDao {
	/**
	 *
	 */
	public MethodOrStandardDao() {
		super();
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
	public Set<String> findNames() {
		return cachedColumnQuery("MethodOrStandard.findAllNames");
	}

}