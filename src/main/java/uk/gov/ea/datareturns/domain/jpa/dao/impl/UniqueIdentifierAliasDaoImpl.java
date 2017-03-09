package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.inject.Inject;

/**
 * DAO for unique identifier aliases
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierAliasDaoImpl extends AbstractEntityDao<UniqueIdentifierAlias> implements UniqueIdentifierAliasDao {

    @Inject
    public UniqueIdentifierAliasDaoImpl(ApplicationEventPublisher publisher) {
        super(UniqueIdentifierAlias.class, publisher);
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    @Override public String generateMash(String inputValue) {
        return inputValue;
    }
}