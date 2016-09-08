package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnPeriod;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ReturnPeriodDao extends EntityDao<ReturnPeriod> {
    public ReturnPeriodDao() {
        super(ReturnPeriod.class);
    }
}