package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Qualifier;

/**
 * DAO for qualifiers
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class QualifierDao extends AbstractJpaDao {

	public QualifierDao() {
		super(Qualifier.class);
	}

	/**
	 * Determine if a qualifier with the given name exists
	 *
	 * @param name the qualifier name to check
	 * @return true if the name exists, false otherwise
	 */
	public boolean nameExists(final String name) {
		return findNames().contains(name);
	}

	/**
	 * Retrieve a full set of qualifier names
	 *
	 * @return a {@link Set} of qualifier names
	 */
	public Set<String> findNames() {
		return cachedColumnQuery("Qualifier.findAllNames");
	}
}