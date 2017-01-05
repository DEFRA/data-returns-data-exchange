package uk.gov.ea.datareturns.domain.jpa.dao;

import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;

/**
 * Extending class for JPA based DAO classes with aliasing
 *
 * @author Graham Willis
 */
public interface AliasingEntityDao<E extends AliasingEntity> extends EntityDao<E> {
    /**
     * Retrieve an entity by its name, ONLY if it is an alias
     *
     * @param alias
     * @return the primary name
     */
    E getByAliasName(Key alias);

    /**
     * Retrieve an entity by its name, REGARDLESS if it is a preferred name or an alias
     * Note that this method returns the entity directly referenced by name or alias.  To retrieve the preferred entity for a given
     * alias see {@link AliasingEntityDao#getPreferred(Key)} below.
     *
     * @param nameOrAlias
     * @return
     */
    E getByNameOrAlias(Key nameOrAlias);

    /**
     * Determine if an entity with the given name or alias exists
     *
     * @param name the method or standard name to check
     * @return true if the name exists, false otherwise
     */
    boolean nameOrAliasExists(Key name);

    /**
     * Given an entity name, lookup the preferred entity for the name
     * If the entity name is actually a preferred name then the entity for that name is returned
     *
     * @param name the name to use to lookup the preferred entity
     * @return the preferred entity for the given name or null of none could be found.
     */
    E getPreferred(Key name);
}
