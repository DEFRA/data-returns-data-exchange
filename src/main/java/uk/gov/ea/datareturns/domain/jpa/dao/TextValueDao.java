package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.TextValue;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class TextValueDao extends AliasingEntityDao {
	public TextValueDao() {
		super(TextValue.class);
	}

	// Allow for no spaces in the method or standard
	protected String getKeyFromRelaxedName(String name) {
		return name.toUpperCase().trim().replaceAll("\\s", "");
	}

}