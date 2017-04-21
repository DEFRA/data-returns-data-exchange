package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UnitDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit;
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
    public UnitDaoImpl(GroupingEntityCommon<Unit> groupingEntityCommon, ApplicationEventPublisher publisher) {
        super(Unit.class, publisher, groupingEntityCommon);

        addSearchField("longName",
                (entity, terms) -> terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(entity.getLongName(), term)));
        addSearchField("type",
                (entity, terms) -> terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(entity.getType(), term)));
    }

    // Just trim units are case sensitive
    public String generateMash(String inputValue) {
        return TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
    }
}