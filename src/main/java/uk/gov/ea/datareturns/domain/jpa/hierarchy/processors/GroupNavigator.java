package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.JpaRepositoryConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupSymbols;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchySymbols;
import uk.gov.ea.datareturns.domain.jpa.repositories.EntityPathUtils;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.MasterDataRepository;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataNaturalKeyService;

import javax.inject.Inject;
import java.util.*;

/**
 * @author Graham Willis
 * Returns a the list of entities at at the next level in the hierarchy to the one being specified skipping
 * any levels where there is no node
 *
 * This variant will operate on groups - enclosed by []
 */
@Component
public class GroupNavigator implements Navigator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupNavigator.class);
    private final JpaRepositoryConfiguration repositoryConfiguration;
    private final MasterDataNaturalKeyService keyService;
    private final GroupCommon groupCommon;

    @Inject
    public GroupNavigator(JpaRepositoryConfiguration repositoryConfiguration, MasterDataNaturalKeyService keyService,
            GroupCommon groupCommon) {
        this.repositoryConfiguration = repositoryConfiguration;
        this.keyService = keyService;
        this.groupCommon = groupCommon;
    }

    /**
     * Navigate to the next level down in the hierarchy
     */
    public Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Map<String, ?> cache,
            Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> hierarchyNodeStringMap) {

        return children(cache, hierarchyLevels, hierarchyNodeStringMap, null, null);
    }

    public Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Map<String, ?> cache,
            Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> entityNames,
            String field, String contains) {

        Iterator<HierarchyLevel<? extends MasterDataEntity>> hierarchyNodesIttr = hierarchyLevels.iterator();
        HierarchyLevel<? extends MasterDataEntity> rootNode = hierarchyNodesIttr.next();
        return down(hierarchyNodesIttr, rootNode, cache, entityNames, new HashMap<>(), field, contains);
    }

    /*
     * Helper function to direct to the map or set evaluator - the hierarchy is terminated by a set
     * So the system has been set up so that initial cache is
     * Map<String, Map<String, Map<String, Set<String>>>>
     * Then as each entity is validated the cache is drilled into so that we get the following
     * sequence
     */
    @SuppressWarnings("unchecked")
    private Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> shim(
            Iterator<HierarchyLevel<? extends MasterDataEntity>> hierarchyNodesIter,
            HierarchyLevel<? extends MasterDataEntity> level,
            Map<String, ?> cache,
            String cacheKey,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> entityNames,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> keys,
            String field, String contains) {

        if (!hierarchyNodesIter.hasNext()) {
            LOGGER.error("Error evaluating hierarchy at : " + level.getControlledList().getDescription());
            return Pair.of(level, null);
        }

        keys.put(level, entityNames.remove(level));

        Object cacheObject = cache.get(cacheKey);
        if (cacheObject instanceof Set) {
            return list(hierarchyNodesIter.next(), (Set<String>) cacheObject, field, contains);
        } else {
            return down(hierarchyNodesIter, hierarchyNodesIter.next(), (Map<String, ?>) cacheObject, entityNames, keys, field,
                    contains);
        }
    }

    private Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> down(
            Iterator<HierarchyLevel<? extends MasterDataEntity>> hierarchyNodesIter,
            HierarchyLevel<? extends MasterDataEntity> level,
            Map<String, ?> cache,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> entityNames,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> keys,
            String field, String contains) {

        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(HierarchySymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then return null
                return Pair.of(level, null);
            } else if (cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so carry error
                return Pair.of(level, null);
            } else if (cache.containsKey(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return shim(hierarchyNodesIter, level, cache, entityNames.get(level), entityNames, keys, field, contains);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(hierarchyNodesIter, level, cache, HierarchySymbols.INCLUDE_ALL_OPTIONALLY, entityNames, keys, field, contains);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(hierarchyNodesIter, level, cache, HierarchySymbols.INCLUDE_ALL, entityNames, keys, field, contains);
            } else if (level instanceof HierarchyGroupLevel) {
                // For a group level search for the group
                if (groupCommon.cacheContainsGroupContainsName((HierarchyGroupLevel<? extends MasterDataEntity>) level, cache,
                        entityNames.get(level))) {
                    String foundGroup = groupCommon
                            .getGroupInCacheFromName((HierarchyGroupLevel<? extends MasterDataEntity>) level, cache,
                                    entityNames.get(level));
                    return shim(hierarchyNodesIter, level, cache, HierarchyGroupSymbols.injectGroup(foundGroup), entityNames, keys, field,
                            contains);
                } else {
                    return Pair.of(level, null);
                }
            } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
                // We should not be here
                return Pair.of(level, null);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, null);
            }
        } else if (entityNames.get(level) == null && cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
            return shim(hierarchyNodesIter, level, cache, HierarchySymbols.EXCLUDE_ALL, entityNames, keys, field, contains);
        } else if (groupCommon.allNulls(entityNames)) {
            return list(level, cache, field, contains);
        } else {
            return Pair.of(level, null);
        }
    }

    private Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> list(
            HierarchyLevel<? extends MasterDataEntity> level,
            Set<String> cache,
            String field, String contains) {

        Collection<? extends MasterDataEntity> itemList = getItemList(level.getHierarchyEntityClass(), field, contains);
        List<MasterDataEntity> resultList = new ArrayList<>();

        if (cache.contains(HierarchySymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.contains(HierarchySymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            // I have split out the branching for clarity
            for (MasterDataEntity e : itemList) {
                String name = keyService.relaxKey(e);
                if (cache.contains(HierarchySymbols.EXCLUDE + name)) {
                    // Explicitly excluded - do nothing
                } else if (cache.contains(HierarchySymbols.INCLUDE_ALL)) {
                    // Item included via wildcard
                    resultList.add(e);
                } else if (cache.contains(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                    // Item included via optional wildcard
                    resultList.add(e);
                } else if (cache.contains(name)) {
                    // Item included by name
                    resultList.add(e);
                } else if (level instanceof HierarchyGroupLevel) {
                    if (groupCommon.cacheContainsGroupContainsName((HierarchyGroupLevel<?>) level, cache, e.getName())) {
                        resultList.add(e);
                    }
                }
            }
        }
        return Pair.of(level, resultList);
    }

    /**
     * Return the subset of entities filtered by the hierarchy
     * @param level The hierarchy level
     * @param cache The cache
     * @return A pair giving the level and the list
     */
    private Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> list(
            HierarchyLevel<? extends MasterDataEntity> level,
            Map<String, ?> cache,
            String field, String contains) {
        Collection<? extends MasterDataEntity> itemList = getItemList(level.getHierarchyEntityClass(), field, contains);
        List<MasterDataEntity> resultList = new ArrayList<>();

        if (cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            for (MasterDataEntity e : itemList) {
                String name = keyService.relaxKey(e);
                if (cache.containsKey(HierarchySymbols.EXCLUDE + name)) {
                    // Explicitly excluded - do nothing
                } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL)) {
                    // Item included via wildcard
                    resultList.add(e);
                } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                    // Item included via optional wildcard
                    resultList.add(e);
                } else if (cache.containsKey(name)) {
                    // Item included by name
                    resultList.add(e);
                } else if (level instanceof HierarchyGroupLevel) {
                    if (groupCommon.cacheContainsGroupContainsName((HierarchyGroupLevel<? extends MasterDataEntity>) level, cache,
                            e.getName())) {
                        resultList.add(e);
                    }
                }
            }
        }
        return Pair.of(level, resultList);
    }

    private <E extends MasterDataEntity> Collection<E> getItemList(Class<E> entityClass, String field, String containing) {
        MasterDataRepository<E> repo = repositoryConfiguration.getMasterDataRepository(entityClass);
        if (field != null && containing != null) {
            StringPath path = EntityPathUtils.getStringPath(entityClass, field);
            return IterableUtils.toList(repo.findAll(path.contains(containing)));
        }
        return repo.findAll();
    }
}
