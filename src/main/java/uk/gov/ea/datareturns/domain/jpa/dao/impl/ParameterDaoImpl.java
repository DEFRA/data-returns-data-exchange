package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.apache.commons.lang3.StringUtils;
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
        addSearchField("cas",
                (entity, terms) -> terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(entity.getCas(), term)));
    }
}