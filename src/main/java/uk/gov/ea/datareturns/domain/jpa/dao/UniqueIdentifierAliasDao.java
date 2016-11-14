package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

/**
 * DAO for return periods
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierAliasDao extends EntityDao<UniqueIdentifierAlias> {

    public UniqueIdentifierAliasDao() {
        super(UniqueIdentifierAlias.class);
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    public String getKeyFromRelaxedName(String name) {
        return name;
    }

    // Just hit the base cache
    public UniqueIdentifierAlias getByNameRelaxed(String name) {
        return getKeyCache().get(name);
    }

}