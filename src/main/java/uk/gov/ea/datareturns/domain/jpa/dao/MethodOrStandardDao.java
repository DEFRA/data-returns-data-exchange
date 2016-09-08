package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.MethodOrStandard;

/**
 * DAO for monitoring methods and standards.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class MethodOrStandardDao extends EntityDao<MethodOrStandard> {
	public MethodOrStandardDao() {
		super(MethodOrStandard.class);
	}

	// Allow for no spaces in the method or standard
	protected String getKeyFromRelaxedName(String name) {
		return name.toUpperCase().trim().replaceAll("\\s", "");
    }

}