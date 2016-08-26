package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

/**
 * DAO for units of measure.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class UnitDao extends AliasingEntityDao {
	public UnitDao() {
		super(Unit.class);
	}

	// Allow for no spaces in the method or standard
	protected String getKeyFromRelaxedName(String name) {
		return name.trim();
	}

	// Override this we don't want to use the key cache here
	@Override
	public String getStandardizedName(final String name) {
		Unit unit = (Unit) getByAlias(name);
		if (unit != null) {
			return unit.getName();
		} else {
			unit = (Unit) getCache().get(name);
			if (unit != null) {
				return unit.getName();
			} else {
				return null;
			}
		}
	}
}