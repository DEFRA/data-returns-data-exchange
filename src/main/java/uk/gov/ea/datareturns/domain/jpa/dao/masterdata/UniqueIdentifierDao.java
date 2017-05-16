package uk.gov.ea.datareturns.domain.jpa.dao.masterdata;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;

import java.util.Set;

/**
 * DAO for Unique Identifiers
 *
 * The Unique identifier (EA_ID) and its aliases do NOT use the
 * standard aliasing mechanism. (This is because the data strucutre
 * differs for the entities uniqueIdentifier, uniqueIdentifierAlias and Site
 *
 * The UniqueIdentifiersService is the service level aggregator for teh functionality
 * connecting these entities
 *
 * @author Graham Willis
 */
public interface UniqueIdentifierDao extends EntityDao<UniqueIdentifier> {
    /**
     * Retrieve an entity by its name, REGARDLESS if it is a preferred name or an alias
     * Note that this method returns the entity directly referenced by name or alias.  To retrieve the preferred entity for a given
     * alias see getPreferred below.
     *
     * @param nameOrAlias
     * @return
     */
    UniqueIdentifier getByNameOrAlias(Key nameOrAlias);

    /**
     * Given a unique identifier, lookup the preferred entity for the identifier
     * If the entity name is actually a preferred name then the entity for that name is returned
     *
     * @param nameOrAlias the identifier to use to lookup the preferred entity
     * @return the preferred entity for the given name or null of none could be found.
     */
    UniqueIdentifier getPreferred(Key nameOrAlias);

    /**
     * Get list of alias names for a given UniqueIdentifier
     * @param uniqueIdentifier
     * @return Alias names
     */
    Set<String> getAliasNames(UniqueIdentifier uniqueIdentifier);

    /**
     * Get a unique identifier from (exact) site name
     */
    Set<UniqueIdentifier> getUniqueIdentifierBySiteName(String siteName);

    /**
     * Test if a unique identifier from its name or alias name
     * @param name
     * @return
     */
    boolean uniqueIdentifierExists(String name);

    /**
     * Get the site from the unique identifier
     * @param uniqueIdentifier
     * @return The site
     */
    Site getSite(UniqueIdentifier uniqueIdentifier);

    /**
     * Get the site from the unique identifier alias
     * @param uniqueIdentifierAlias
     * @return the site
     */
    Site getSite(UniqueIdentifierAlias uniqueIdentifierAlias);

    /**
     * Get the list of all permit numbers from the uniqueIdentifierName
     * @param name unique identifier name
     * @return List of all permit numbers
     */
    Set<String> getAllUniqueIdentifierNames(String name);
}
