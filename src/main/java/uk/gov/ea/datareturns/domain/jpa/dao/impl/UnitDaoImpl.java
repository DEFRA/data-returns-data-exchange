package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.UnitDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupingEntityCommon;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.inject.Inject;

/**
 * DAO for units of measure.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UnitDaoImpl extends AbstractAliasingEntityDao<Unit> implements UnitDao {

    @Inject
    public UnitDaoImpl(GroupingEntityCommon<Unit> groupingEntityCommon) {
        super(Unit.class, groupingEntityCommon);
    }

    // Just trim units are case sensitive
    public String generateMash(String inputValue) {
        return TextUtils.normalize(inputValue);
    }
}