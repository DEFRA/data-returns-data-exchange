package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;

/**
 * DAO for return types.
 *
 * @author Graham Willis
 */
@Repository
public class ReleasesAndTransfersDao extends EntityDao<ReleasesAndTransfers> {
    public ReleasesAndTransfersDao() {
        super(ReleasesAndTransfers.class);
    }
}