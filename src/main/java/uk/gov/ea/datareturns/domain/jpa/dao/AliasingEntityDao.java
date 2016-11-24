package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Extending class for JPA based DAO classes with aliasing
 *
 * @author Graham Willis
 */
public abstract class AliasingEntityDao<E extends AliasingEntity> extends EntityDao<E> {

    private volatile Map<String, E> cacheByAlias = null;
    private volatile Map<String, E> cacheByAliasKey = null;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     * @param entityClass
     */
    public AliasingEntityDao(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * This invocation of the standardized name will convert both aliases and cased converted
     * entries
     *
     * @param name - an alias or case inaccurate search term
     * @return The standardized (controlled-list) name
     */
    public String getStandardizedName(final String name) {
        E e = getAliasKeyCache().get(getKeyFromRelaxedName(name));
        if (e != null) {
            return e.getName();
        } else {
            e = getKeyCache().get(getKeyFromRelaxedName(name));
            if (e != null) {
                return e.getName();
            }
            return null;
        }
    }

    /**
     * To retrieve the preferred name from the supplied alias
     * @param alias
     * @return the primary name
     */
    public E getByAlias(String alias) {
        return getAliasCache().get(alias);
    }

    /**
     * This override of list() calculates the aliases from the preferred list
     * @return the processed list. So the list only contains the primaries
     */
    @Override
    public List<E> list() {
        // Get the list from the cached master list in EntityDao
        List<E> list = super.list();
        return aliasProcessor(list);
    }

    @Override
    public List<E> list(Predicate<E> predicate) {
        List<E> list = super.list(predicate);
        return aliasProcessor(list);
    }

    private List<E> aliasProcessor(List<E> list) {
        // Split the stream into aliases and basis; with and without preferred set
        List<E> basis = list.stream().filter(e -> e.getPreferred() == null).collect(Collectors.toList());
        List<E> aliases = list.stream().filter(e -> e.getPreferred() != null).collect(Collectors.toList());
        // Collect the aliases into groups by preferred name
        Map<String, Set<String>> aliasesByName = aliases.stream().collect(
                Collectors.groupingBy(E::getPreferred,
                        Collectors.mapping(E::getName, Collectors.toSet())
                )
        );
        // Go through the basis list and look up the name in the aliases
        // on aliasesByName and append them to name
        List<E> result = basis.stream().map(e -> {
            Set<String> aliasList = aliasesByName.get(e.getName());
            if (aliasList != null) {
                e.setAliases(aliasList);
            }
            return e;
        }).collect(Collectors.toList());

        return result;
    }

    /**
     * Add an entity <E> to the persistence
     * Note - not calling super.add because of side effects of nested @Transactional
     * @param entity
     */
    @Transactional
    public void add(E entity) {
        entityManager.persist(entity);
        entityManager.flush();
        LOGGER.info("Added: " + entityClass.getSimpleName() + "id: " + entity.getId() + " " + entity.getName());
        super.clearCache();
        clearCache();
    }

    /**
     * Remove an entity of type <E>
     *
     * @param id The entity identifier
     * @throws IllegalArgumentException
     *
     * Note not calling super.removeById because of side effects of nested @Transactional
     */
    @Transactional
    @Override
    public void removeById(long id) throws IllegalArgumentException {
        E entity = getById(id);
        entityManager.remove(entity);
        LOGGER.info("Deleted: " + entityClass.getSimpleName() + " ID: " + id);
        super.clearCache();
        clearCache();
    }

    private Map<String, E> getAliasCache() {
        if (cacheByAlias == null) {
            synchronized (this) {
                if (cacheByAlias == null) {
                    LOGGER.info("Build alias name cache of: " + entityClass.getSimpleName());
                    List<E> list = super.list();
                    List<E> aliases = list.stream().filter(e -> e.getPreferred() != null).collect(Collectors.toList());
                    Map<String, E> basis = list.stream().filter(e -> e.getPreferred() == null)
                            .collect(Collectors.toMap(AliasingEntity::getName, e -> e));
                    cacheByAlias = aliases.stream().collect(Collectors.toMap(AliasingEntity::getName, e -> basis.get(e.getPreferred())));
                }
            }
        }
        return cacheByAlias;
    }

    protected Map<String, E> getAliasKeyCache() {
        if (cacheByAliasKey == null) {
            Map<String, E> localCache = getAliasCache();
            synchronized (this) {
                if (cacheByAliasKey == null) {
                    LOGGER.info("Build alias key cache of: " + entityClass.getSimpleName());
                    cacheByAliasKey = localCache.entrySet().stream()
                            .collect(Collectors.toMap(e -> getKeyFromRelaxedName(e.getKey()), Map.Entry::getValue));
                }
            }
        }
        return cacheByAliasKey;
    }

    protected void clearCache() {
        if (cacheByAlias != null) {
            synchronized (this) {
                if (cacheByAlias != null) {
                    cacheByAlias = null;
                    LOGGER.info("Clear alias name cache: " + entityClass.getSimpleName());
                }
            }
        }
        if (cacheByAliasKey != null) {
            synchronized (this) {
                if (cacheByAliasKey != null) {
                    cacheByAliasKey = null;
                    LOGGER.info("Clear alias key cache: " + entityClass.getSimpleName());
                }
            }
        }
    }

}
