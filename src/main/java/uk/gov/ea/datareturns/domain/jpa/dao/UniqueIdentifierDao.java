package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class UniqueIdentifierDao extends EntityDao<UniqueIdentifier> {
	public UniqueIdentifierDao() {
		super(UniqueIdentifier.class);
	}
}