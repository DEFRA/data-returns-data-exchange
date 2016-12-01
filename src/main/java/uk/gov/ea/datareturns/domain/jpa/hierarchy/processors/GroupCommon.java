package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupSymbols;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group functions for entities extending Hierarchy.GroupedHierarchyEntity
 * @author Graham Willis
 */
class GroupCommon {
    private static <E extends Hierarchy.GroupedHierarchyEntity> EntityDao<E> getEntityDaoFromLevel(HierarchyGroupLevel<E> level) {
        Class<? extends EntityDao<E>> listItemDaoClass = level.getDaoClass();
        return SpringApplicationContextProvider.getApplicationContext().getBean(listItemDaoClass);
    }

    /**
     * For grouped levels we need to test if the cache contains a group and for which the
     * entity name is a member of the group
     * @param level The grouped hierarchy level
     * @param cache The cache
     * @param entityName The given enity name
     * @return true if entity name is found
     */
    static <E extends Hierarchy.GroupedHierarchyEntity> boolean cacheContainsGroupContainsName(HierarchyGroupLevel<E> level, Set<String> cache, String entityName) {
        EntityDao<E> dao = getEntityDaoFromLevel(level);
        GroupingEntityCommon<?> groupingEntityCommon = dao.getGroupingEntityCommon();
        Set<String> groups = findGroups(cache, groupingEntityCommon);
        if (groups.size() == 0) {
            return false;
        } else {
            for (String group : groups) {
                if (groupingEntityCommon.isGroupMember(group, entityName)) {
                    return true;
                }
            }
            return false;
        }
    }


    static <E extends Hierarchy.GroupedHierarchyEntity> boolean cacheContainsGroupContainsName(HierarchyGroupLevel<E> level, Map<String, ?> cache, String entityName) {
        Set<String> cacheSet = cache.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return cacheContainsGroupContainsName(level, cacheSet, entityName);
    }

    /**
     * Find the group in the cache that corresponds to the supplied entity name
     * @param level The hierarchy level
     * @param cache The cache
     * @param entityName The entity name
     * @return The group
     */
    private static <E extends Hierarchy.GroupedHierarchyEntity> String getGroupInCacheFromName(HierarchyGroupLevel<E> level, Set<String> cache, String entityName) {
        EntityDao<E> dao = getEntityDaoFromLevel(level);
        GroupingEntityCommon<?> unitUnitDaoGroupingEntityCommon = dao.getGroupingEntityCommon();
        Set<String> groups = findGroups(cache, unitUnitDaoGroupingEntityCommon);
        if (groups.size() == 0) {
            return null;
        } else {
            for (String group : groups) {
                if (unitUnitDaoGroupingEntityCommon.isGroupMember(group, entityName)) {
                    return group;
                }
            }
            return null;
        }
    }

    /**
     * Find the groups on the hierarchy level
     * @param cache The cache
     * @return A set of groups
     */
    static private <E extends Hierarchy.GroupedHierarchyEntity> Set<String> findGroups(Set<String> cache, GroupingEntityCommon<?> unitUnitDaoGroupingEntityCommon) {
        // Get the list of groups associated with the entity at this level
        Set<String> levelGroups = unitUnitDaoGroupingEntityCommon.listGroups();

        // Get the list of groups at this level in the cache
        Set<String> cacheGroups = cache.stream()
                .filter(HierarchyGroupSymbols::isGroup)
                .map(HierarchyGroupSymbols::extractGroup)
                .collect(Collectors.toSet());

        // Get the groups appearing in both sets.
        Set<String> groups = new HashSet<>(levelGroups);
        groups.retainAll(cacheGroups);

        return groups;
    }

    public static String getGroupInCacheFromName(HierarchyGroupLevel level, Map<String, ?> cache, String entityName) {
        Set<String> cacheSet = cache.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return getGroupInCacheFromName(level, cacheSet, entityName);
    }

    static boolean allNulls(Map<?, ?> m) {
        Set ks = m.keySet();
        for (Object key: ks) {
            if (m.get(key) != null) {
                return false;
            }
        }
        return true;
    }
}
