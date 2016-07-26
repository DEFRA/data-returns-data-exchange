package uk.gov.ea.datareturns.domain.jpa.dao;

import java.util.Set;

import org.springframework.stereotype.Repository;

import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

/**
 * DAO for unique identifiers (EaId)
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class UniqueIdentifierDao extends AbstractJpaDao {

	public UniqueIdentifierDao() {
		super(UniqueIdentifier.class);
	}

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
}