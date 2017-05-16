package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReferencePeriod;

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
    public ReferencePeriodDaoImpl() {
        super(ReferencePeriod.class);
    }
}