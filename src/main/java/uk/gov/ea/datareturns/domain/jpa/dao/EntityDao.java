package uk.gov.ea.datareturns.domain.jpa.dao;

import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupingEntityCommon;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Common DAO Interface for controlled list entities
 *
 * @author Sam Gardner-Dell
 */
public interface EntityDao<E extends ControlledListEntity> {

    GroupingEntityCommon<? extends Hierarchy.GroupedHierarchyEntity> getGroupingEntityCommon();

    /**
     * Get an entity of type <E> by its unique identifier
     *
     * @param id The entity identifier
     * @return E
     */
    E getById(long id);

    /**
     * Retrieve an entity by its name
     *
     * @param name the name of entity to retrieve
     * @return The entity E or null
     */
    E getByName(String name);

    /**
     * Retrieve an entity using the given {@link Key}
     *
     * @param name the key to retrieve the entity
     * @return The entity E or null
     */
    E getByName(Key name);

    /**
     * Determine if an entity with the given name exists
     *
     * @param name the method or standard name to check
     * @return true if the name exists, false otherwise
     */
    boolean nameExists(Key name);

    /**
     * List all entities of type E. This list invocation includes any aliases as primaries
     *
     * @return List<E>
     */
    List<E> list();

    /**
     * List all entities of type <E> which satisfy a predicate
     *
     * @param predicate The predicate
     * @return List<E>
     */
    List<E> list(Predicate<E> predicate);

    /**
     * List the entities of type <E> filtered such that the field contains the search term
     *
     * @param field The name of the field used for filtering
     * @param contains The search term
     * @return A filtered list
     */
    List<E> list(String field, String contains);

    /**
     * Search for entities which match ANY of the given search terms.
     *
     * The nature of the search is specific to the entity.  All entities will search names but specific subclasses may customise the
     * implementation, for example to search aliases, or for parameters to search CAS numbers.
     *
     * @param terms the terms to search for
     * @return a {@link List} of matching entities.
     */
    List<E> search(List<String> terms);


    /**
     * @return the set of field identifiers that are searchable for the entity <E>
     */
    Set<String> getSearchFields();

    /**
     * Retrieves a mashed version of the name from a cache.
     *
     * The name must exist in the cache or null will be returned.
     *
     * @param name the name by which to lookup a mash
     * @return the mashed version of the name if one is found in the cache, null otherwise
     */
    String lookupMashFromName(String name);

    /**
     * Retrieves a name from the cache given an exact mashed value
     * The mash must exist in the cache or null will be returned.
     *
     * @param mash a mashed version of a name (space reduced/normalised according to entity specific rules)
     * @return the proper name for the given mash if one is found in the cache, null otherwise
     */
    String lookupNameFromMash(final String mash);

    /**
     * A method to convert the reduce variation in name in case and spacing
     * to a standard format which acts as the key. Here so it can be overridden. The default functionality
     * is to convert to upper cases and reduce multiple spaces to a single space to create the lookup key.
     */
    String generateMash(String input);

    /**
     * Add a new entity of type <E>
     *
     */
    void add(E entity);

    /**
     * Remove an entity of type <E>
     *
     * @param id The entity identifier
     * @throws IllegalArgumentException
     */
    void removeById(long id) throws IllegalArgumentException;

    /**
     * Get the class of the entity being operated on
     */
    Class<E> getEntityClass();

    /**
     * Clear the (local) caches
     */
    void clearCaches();

    /**
     * Retrieve a DAO for a given DAO class
     * This is intended to allow access to the DAO's from a non-spring managed context
     *
     * @param daoClass the desired dao class
     * @return the spring managed dao for the given class
     */
    static <T extends EntityDao<? extends ControlledListEntity>> T getDao(Class<T> daoClass) {
        return SpringApplicationContextProvider.getApplicationContext().getBean(daoClass);
    }
}
