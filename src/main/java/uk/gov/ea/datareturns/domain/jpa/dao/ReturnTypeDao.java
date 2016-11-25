package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupingEntityCommon;

/**
 * DAO for return types.
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReturnTypeDao extends EntityDao<ReturnType> {
    public ReturnTypeDao() {
        super(ReturnType.class, new GroupingEntityCommon<>());
    }
}