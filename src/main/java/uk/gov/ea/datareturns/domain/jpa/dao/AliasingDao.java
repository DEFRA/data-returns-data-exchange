package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by graham on 16/08/16.
 */
public class AliasingDao<E extends AliasingEntity> extends AbstractJpaDao {

    protected volatile Map<String, E> cacheByAlias = null;
    protected volatile Map<String, E> cacheByUpperCaseAlias = null;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     * @param entityClass
     */
    AliasingDao(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * This override of list() calculates the aliases from the preferred list
     * @return the processed list. So the list only contains the primaries
     */
    @Override
    public List<E> list() {
        // Get the list from the cached master list in AbstractJpaDao
        List<E> list = super.list();
        return aliasProcessor(list);
    }

    @Override
    public List<E> list(Predicate predicate) {
        List<E> list = super.list(predicate);
        return aliasProcessor(list);
    }

    /**
     * This invocation of the standardized name will convert both aliases and cased converted
     * entries
     *
     * @param name - an alias or case inaccurate search term
     * @return The standardized (controlled-list) name
     */
    public String getStandardizedName(final String name) {
        E e = getUpperCaseAliasCache().get(name.toUpperCase());
        if (e != null) {
            return e.getName();
        } else {
            e = (E) getUpperCaseCache().get(name.toUpperCase());
            if (e != null) {
                return e.getName();
            }
            return null;
        }
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
     * To retrieve the preferred name from the supplied alias
     * @param alias
     * @return the primary name
     */
    public E getByAlias(String alias) {
        return getAliasCache().get(alias);
    }

    private Map<String, E> getAliasCache() {
        if (cacheByAlias == null) {
            synchronized(this) {
                if (cacheByAlias == null) {
                    LOGGER.info("Build alias cache of: " + entityClass.getSimpleName());
                    List<E> list = super.list();
                    List<E> aliases = list.stream().filter(e -> e.getPreferred() != null).collect(Collectors.toList());
                    cacheByAlias = aliases.stream().collect(Collectors.toMap(e -> e.getName(), e -> (E) super.getByName(e.getPreferred())));
                } 
            }
        }
        return cacheByAlias;
    }

    protected Map<String, E> getUpperCaseAliasCache() {
        if (cacheByUpperCaseAlias == null) {
            Map<String, E> localCache = getAliasCache();
            synchronized(this) {
                if (cacheByUpperCaseAlias == null) {
                    LOGGER.info("Build case insensitive alias cache of: " + entityClass.getSimpleName());
                    cacheByUpperCaseAlias = localCache.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toUpperCase(), e -> e.getValue()));
                }
            }
        }
        return cacheByUpperCaseAlias;
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
        E entity = (E) getById(id);
        entityManager.remove(entity);
        LOGGER.info("Deleted: " + entityClass.getSimpleName() + " ID: " + id);
        super.clearCache();
        clearCache();
    }

    public void clearCache() {
        if (cacheByAlias != null) {
            synchronized (this) {
                if (cacheByAlias != null) {
                    cacheByAlias = null;
                    LOGGER.info("Clear cacheByAlias: " + entityClass.getSimpleName());
                }
            }
        }
        if (cacheByUpperCaseAlias != null) {
            synchronized (this) {
                if (cacheByUpperCaseAlias != null) {
                    cacheByUpperCaseAlias = null;
                    LOGGER.info("Clear cacheByUpperCaseAlias: " + entityClass.getSimpleName());
                }
            }
        }
    }

}
