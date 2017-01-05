package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchySymbols;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import java.util.*;

/**
 * @author Graham Willis
 * Returns a the list of entities at at the next level in the hierarchy to the one being specified skipping
 * any levels where there is no node
 */
@Component
public class SimpleNavigator implements Navigator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNavigator.class);

    public Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> children(
            Map<String, ?> cache,
            Set<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> hierarchyNodeStringMap) {

        return children(cache, hierarchyLevels, hierarchyNodeStringMap, null, null);
    }

    public Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> children(
            Map<String, ?> cache,
            Set<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames,
            String field, String contains) {

        Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr = hierarchyLevels.iterator();
        HierarchyLevel<? extends Hierarchy.HierarchyEntity> rootNode = hierarchyNodesIttr.next();
        return down(hierarchyNodesIttr, rootNode, cache, entityNames, new HashMap<>(), field, contains);
    }

    /*
     * Helper function to direct to the map or set evaluator - the hierarchy is terminated by a set
     * So the system has been set up so that initial cache is
     * Map<String, Map<String, Map<String, Set<String>>>>
     * Then as each entity is validated the cache is drilled into so that we get the following
     * sequence
     */
    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> shim(
            Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr,
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Map<String, ?> cache,
            String cacheKey,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> keys,
            String field, String contains) {

        if (!hierarchyNodesIttr.hasNext()) {
            LOGGER.error("Error evaluating hierarchy at : " + level.getControlledList().getDescription());
            return Pair.of(level, null);
        }

        keys.put(level, entityNames.remove(level));

        if (cache.get(cacheKey) instanceof Set) {
            return list(hierarchyNodesIttr.next(), (Set<String>)cache.get(cacheKey), field, contains);
        } else {
            return down(hierarchyNodesIttr, hierarchyNodesIttr.next(), (Map<String, ?>)cache.get(cacheKey), entityNames, keys, field, contains);
        }
    }

    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> down(
            Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr,
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Map<String, ?> cache,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> keys,
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
                return shim(hierarchyNodesIttr, level, cache, entityNames.get(level), entityNames, keys, field, contains);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.INCLUDE_ALL_OPTIONALLY, entityNames, keys, field, contains);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.INCLUDE_ALL, entityNames, keys, field, contains);
            } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
                // We should not be here
                return Pair.of(level, null);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, null);
            }
        } else if (entityNames.get(level) == null && cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
            return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.EXCLUDE_ALL, entityNames, keys, field, contains);
        } else if (GroupCommon.allNulls(entityNames)) {
            return list(level, cache, field, contains);
        } else {
            return Pair.of(level, null);
        }
    }

    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> list(
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Set<String> cache,
            String field, String contains) {

        // Get the Dao from the level
        Class<? extends EntityDao> listItemDaoClass = level.getDaoClass();

        EntityDao<? extends Hierarchy.HierarchyEntity> dao = SpringApplicationContextProvider.getApplicationContext().getBean(listItemDaoClass);
        List<? extends Hierarchy.HierarchyEntity> itemList = (field != null && contains != null) ? dao.list(field, contains) : dao.list();
        List<Hierarchy.HierarchyEntity> resultList = new ArrayList<>();

        if (cache.contains(HierarchySymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.contains(HierarchySymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            // I have split out the branching for clarity
            for (Hierarchy.HierarchyEntity e : itemList) {
                String name = dao.generateMash(e.getName());
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
    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, List<? extends Hierarchy.HierarchyEntity>> list(
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Map<String, ?> cache,
            String field, String contains) {

        // Get the Dao from the level
        Class<? extends EntityDao> listItemDaoClass = level.getDaoClass();

        // Get the DAO from spring
        EntityDao<? extends Hierarchy.HierarchyEntity> dao = SpringApplicationContextProvider.getApplicationContext().getBean(listItemDaoClass);
        List<? extends Hierarchy.HierarchyEntity> itemList = (field != null && contains != null) ? dao.list(field, contains) : dao.list();
        List<Hierarchy.HierarchyEntity> resultList = new ArrayList<>();

        if (cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            for (Hierarchy.HierarchyEntity e : itemList) {
                String name = dao.generateMash(e.getName());
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
                }
            }
        }
        return Pair.of(level, resultList);
    }
}
