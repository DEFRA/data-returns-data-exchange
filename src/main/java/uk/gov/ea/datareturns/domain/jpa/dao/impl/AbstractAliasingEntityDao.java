package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupingEntityCommon;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extending class for JPA based DAO classes with aliasing
 *
 * @author Graham Willis
 */
public abstract class AbstractAliasingEntityDao<E extends AliasingEntity> extends AbstractEntityDao<E> implements
        uk.gov.ea.datareturns.domain.jpa.dao.AliasingEntityDao<E> {
    protected final String CACHE_PREFERRED_ENTITIES = "PREFERRED_ENTITIES";
    protected final String CACHE_ALIAS_ENTITIES = "ALIAS_ENTITIES";

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     * @param entityClass
     */
    public AbstractAliasingEntityDao(Class<E> entityClass) {
        this(entityClass, null);
    }

    public AbstractAliasingEntityDao(Class<E> entityClass,
            GroupingEntityCommon<? extends Hierarchy.GroupedHierarchyEntity> groupedHierarchyEntity) {
        super(entityClass, groupedHierarchyEntity);

        addSearchField("aliases", (entity, terms) -> {
            if (entity.getAliases() != null) {
                for (String alias : entity.getAliases()) {
                    if (terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(alias, term))) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Override public E getByAliasName(Key alias) {
        return getCache().forView(CACHE_ALIAS_ENTITIES).get(usingKey(alias));
    }

    @Override public E getByNameOrAlias(Key nameOrAlias) {
        E entity = super.getByName(nameOrAlias);
        if (entity == null) {
            entity = getByAliasName(nameOrAlias);
        }
        return entity;
    }

    @Override public boolean nameOrAliasExists(Key name) {
        return getByNameOrAlias(name) != null;
    }

    @Override public E getPreferred(Key name) {
        E val = getByNameOrAlias(name);
        while (val != null && val.getPreferred() != null) {
            val = getCache().forView(CACHE_ALL_ENTITIES).get(val.getPreferred());
        }
        return val;
    }

    protected EntityCache<String, E> cacheBuilder() {
        // Fetch all entities and create a map by entity name so that they can be processed easily
        Map<String, E> allEntities = super.fetchAll().stream().collect(Collectors.toMap(E::getName, e -> e));

        // Create a map of primary values to the set of aliases which belong to those primary values
        Map<String, Set<String>> aliasesByName = allEntities.values().stream()
                .filter(AliasingEntity::isAlias)
                .collect(
                        Collectors.groupingBy(E::getPreferred,
                                Collectors.mapping(E::getName, Collectors.toSet())
                        )
                );

        // Now that we have a map of primary values to a set of aliases we can decorate the root cache before returning
        for (Map.Entry<String, Set<String>> entry : aliasesByName.entrySet()) {
            String primaryName = entry.getKey();
            Set<String> aliases = Optional.of(entry.getValue()).orElse(new HashSet<>());
            allEntities.get(primaryName).setAliases(aliases);
        }

        /*
        Now all entities have been preprocessed, we can build the caches

        Note that for the this implementation, the preferred entities are set as the cache default.
         */
        EntityCache<String, E> data = EntityCache.build(allEntities.values(),
                EntityCache.View.of(CACHE_PREFERRED_ENTITIES, AliasingEntity::getName, AliasingEntity::isPrimary),
                EntityCache.View.of(CACHE_ALL_ENTITIES, AliasingEntity::getName),
                EntityCache.View.of(CACHE_ALIAS_ENTITIES, AliasingEntity::getName, AliasingEntity::isAlias)
        );
        return data;
    }
}