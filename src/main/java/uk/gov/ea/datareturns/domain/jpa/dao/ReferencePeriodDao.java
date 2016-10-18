package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReferencePeriod;

/**
 * DAO for reference periods.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReferencePeriodDao extends AliasingEntityDao<ReferencePeriod> {
    public ReferencePeriodDao() {
        super(ReferencePeriod.class);
    }
}