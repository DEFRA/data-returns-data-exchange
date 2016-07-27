package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;

/**
 * DAO for return types.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ReturnTypeDao extends AbstractJpaDao {

	public ReturnTypeDao() {
		super(ReturnType.class);
	}

//	/**
//	 * Determine if a Return Type with the given name exists
//	 *
//	 * @param name the return type name to check
//	 * @return true if the name exists, false otherwise
//	 */
//	public boolean nameExists(final String name) {
//		return findNames().contains(name);
//	}
//
//	/**
//	 * Retrieve a full set of return type names
//	 *
//	 * @return a {@link Set} of return type names
//	 */
//	public Set<String> findNames() {
//		return cachedColumnQuery("ReturnType.findAllNames");
//	}
}