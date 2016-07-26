package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;

/**
 * DAO for parameters
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ParameterDao extends AbstractJpaDao {

	public ParameterDao() {
		super(Parameter.class);
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