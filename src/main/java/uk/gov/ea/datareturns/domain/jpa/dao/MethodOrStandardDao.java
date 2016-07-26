package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.MethodOrStandard;

/**
 * DAO for monitoring methods and standards.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class MethodOrStandardDao extends AbstractJpaDao {

	public MethodOrStandardDao() {
		super(MethodOrStandard.class);
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