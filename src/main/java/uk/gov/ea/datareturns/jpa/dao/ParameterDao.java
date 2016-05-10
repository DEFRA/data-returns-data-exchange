package uk.gov.ea.datareturns.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ParameterDao extends AbstractJpaDao {
	/**
	 *
	 */
	public ParameterDao() {
		super();
	}

	/**
	 * Determine if a parameter with the given name exists
	 *
	 * @param name the parameter name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of parameter names
	 *
	 * @return a {@link Set} of parameter names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("Parameter.findAllNames");
	}

}