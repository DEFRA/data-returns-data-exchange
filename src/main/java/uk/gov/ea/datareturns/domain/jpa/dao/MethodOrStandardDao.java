package uk.gov.ea.datareturns.domain.jpa.dao;

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
}