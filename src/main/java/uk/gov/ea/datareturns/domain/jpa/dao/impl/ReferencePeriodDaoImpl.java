package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ReferencePeriod;

import javax.inject.Inject;

/**
 * DAO for reference periods.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReferencePeriodDaoImpl extends AbstractAliasingEntityDao<ReferencePeriod> implements ReferencePeriodDao {
    @Inject
    public ReferencePeriodDaoImpl(ApplicationEventPublisher publisher) {
        super(ReferencePeriod.class, publisher);
    }
}