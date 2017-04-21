package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;

import javax.inject.Inject;

/**
 * DAO for parameters
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ParameterDaoImpl extends AbstractAliasingEntityDao<Parameter> implements ParameterDao {
    @Inject
    public ParameterDaoImpl(ApplicationEventPublisher publisher) {
        super(Parameter.class, publisher);
        addSearchField("cas",
                (entity, terms) -> terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(entity.getCas(), term)));
    }
}