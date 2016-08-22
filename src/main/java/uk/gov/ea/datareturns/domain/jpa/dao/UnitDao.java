package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

/**
 * DAO for units of measure.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class UnitDao extends EntityDao {
	public UnitDao() {
		super(Unit.class);
	}
}