package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.QualifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Qualifier;

import javax.inject.Inject;

/**
 * DAO for qualifiers
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QualifierDaoImpl extends AbstractEntityDao<Qualifier> implements QualifierDao {
    @Inject
    public QualifierDaoImpl() {
        super(Qualifier.class);
    }
}