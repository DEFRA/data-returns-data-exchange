package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;

/**
 * DAO for return types.
 *
 * @author Graham Willis
 */
@Repository
public class ReturnTypeDao extends EntityDao<ReturnType> {
    public ReturnTypeDao() {
        super(ReturnType.class);
    }
}