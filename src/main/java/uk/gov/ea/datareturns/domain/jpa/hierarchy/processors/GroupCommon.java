package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import uk.gov.ea.datareturns.domain.jpa.dao.GroupingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupSymbols;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by graham on 22/11/16.
 */
public class GroupCommon {
    /**
     * For grouped levels we need to test if the cache contains a group and for which the
     * entity name is a member of the group
     * @param level The grouped hierarchy level
     * @param cache The cache
     * @param entityName The given enity name
     * @return true if entity name is found
     */
     static boolean cacheContainsGroupContainsName(HierarchyGroupLevel level, Set<String> cache, String entityName) {
        // Get the Dao from the level
        Class<? extends GroupingEntityDao<? extends Hierarchy.GroupedHierarchyEntity>> listItemDaoClass = level.getDaoClass();

        // Get the DAO from spring
        GroupingEntityDao<? extends Hierarchy.GroupedHierarchyEntity> dao = SpringApplicationContextProvider.getApplicationContext().getBean(listItemDaoClass);

        // Get the list of groups associated with the entity at this level
        Set<String> levelGroups = dao.listGroups();

        // Get the list of groups at this level in the cache
        Set<String> cacheGroups = cache.stream()
                .filter(c -> HierarchyGroupSymbols.isGroup(c))
                .map(c -> HierarchyGroupSymbols.extractGroup(c))
                .collect(Collectors.toSet());

        // Get the groups appearing in both sets.
        Set<String> groups = new HashSet<>(levelGroups);
        groups.retainAll(cacheGroups);

        if (groups.size() == 0) {
            // There are no intersecting sets
            return false;
        } else {
            for (String group : groups) {
                if (dao.isGroupMember(group, entityName)) {
                    return true;
                }
            }
            return false;
        }
    }

    static boolean cacheContainsGroupContainsName(HierarchyGroupLevel level, Map<String, String> cache, String entityName) {
        Set<String> cacheSet = cache.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return cacheContainsGroupContainsName(level, cacheSet, entityName);
    }

    static boolean allNulls(Map m) {
        Set ks = m.keySet();
        for (Object key: ks) {
            if (m.get(key) != null) {
                return false;
            }
        }
        return true;
    }
}
