package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Qualifier;

/**
 * DAO for qualifiers
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class QualifierDao extends EntityDao<Qualifier> {
	public QualifierDao() {
		super(Qualifier.class);
	}
}