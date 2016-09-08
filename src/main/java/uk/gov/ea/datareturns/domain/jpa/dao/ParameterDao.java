package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;

/**
 * DAO for parameters
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ParameterDao extends AliasingEntityDao<Parameter> {
	public ParameterDao() {
		super(Parameter.class);
	}
}