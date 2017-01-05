package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;

/**
 * DAO for parameters
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ParameterDaoImpl extends AbstractAliasingEntityDao<Parameter> implements ParameterDao {
    public ParameterDaoImpl() {
        super(Parameter.class);
    }
}