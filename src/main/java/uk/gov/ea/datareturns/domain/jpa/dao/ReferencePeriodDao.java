package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReferencePeriod;

/**
 * DAO for reference periods.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ReferencePeriodDao extends AliasingEntityDao<ReferencePeriod> {
    public ReferencePeriodDao() {
        super(ReferencePeriod.class);
    }
}