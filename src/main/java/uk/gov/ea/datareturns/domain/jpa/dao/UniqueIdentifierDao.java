package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

/**
 * DAO for return periods
 *
 * @author Graham Willis
 *
 * The Unique identifier (EA_ID) and its aliases do NOT use the
 * standard aliasing mechanism. (This is because the data strucutre
 * differs for the entities uniqueIdentifier, uniqueIdentifierAlias and Site
 *
 * The UniqueIdentifiersService is the service level aggregator for teh functionality
 * connecting these entities

 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierDao extends EntityDao<UniqueIdentifier> {

    public UniqueIdentifierDao() {
        super(UniqueIdentifier.class);
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    public String getKeyFromRelaxedName(String name) {
        return name;
    }

    // Just hit the base cache
    public UniqueIdentifier getByNameRelaxed(String name) {
        return getKeyCache().get(name);
    }
}