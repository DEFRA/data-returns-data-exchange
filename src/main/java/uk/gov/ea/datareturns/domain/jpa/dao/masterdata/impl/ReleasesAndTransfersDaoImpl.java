package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReleasesAndTransfers;

import javax.inject.Inject;

/**
 * DAO for releases and transfers
 *
 * @author Graham Willis
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReleasesAndTransfersDaoImpl extends AbstractEntityDao<ReleasesAndTransfers> implements ReleasesAndTransfersDao {
    @Inject
    public ReleasesAndTransfersDaoImpl() {
        super(ReleasesAndTransfers.class);
    }
}