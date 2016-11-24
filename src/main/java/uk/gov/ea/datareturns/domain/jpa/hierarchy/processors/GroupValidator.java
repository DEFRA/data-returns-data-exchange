package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Graham Willis
 * Validate a set of entities against a specific hierarchy the result of the validation
 * and the lowest level in the hierarch reached to obtain the result
 *
 * This variant will operate on groups - enclosed by []
 */
@Component
public class GroupValidator implements Validator {
    private Iterator<HierarchyLevel> hierarchyNodesIttr;

    public Pair<HierarchyLevel, Hierarchy.Result> validate(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> entityNames) {
        hierarchyNodesIttr = hierarchyLevels.iterator();
        HierarchyLevel rootNode = hierarchyNodesIttr.next();
        return evaluate(rootNode, cache, entityNames);
    }

    /*
     * Main evaluating function which is recursive as the rules are the same for each entity
     */
    private Pair<HierarchyLevel, Hierarchy.Result> evaluate(HierarchyLevel level, Map cache, Map<HierarchyLevel, String> entityNames) {
        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(HierarchyGroupSymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Hierarchy.Result.EXCLUDED);
            } else if (cache.containsKey(HierarchyGroupSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Hierarchy.Result.NOT_EXPECTED);
            } else if (cache.containsKey(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return shim(level, cache, entityNames.get(level), entityNames);
            } else if(cache.containsKey(HierarchyGroupSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(level, cache, HierarchySymbols.INCLUDE_ALL_OPTIONALLY, entityNames);
            } else if (cache.containsKey(HierarchyGroupSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(level, cache, HierarchyGroupSymbols.INCLUDE_ALL, entityNames);
            } else if (level instanceof HierarchyGroupLevel) {
                // For a group level search for the group
                if (GroupCommon.cacheContainsGroupContainsName((HierarchyGroupLevel)level, cache, entityNames.get(level))) {
                    String foundGroup = GroupCommon.getGroupInCacheFromName((HierarchyGroupLevel)level, cache, entityNames.get(level));
                    return shim(level, cache, HierarchyGroupSymbols.injectGroup(foundGroup), entityNames);
                } else {
                    return Pair.of(level, Hierarchy.Result.NOT_IN_GROUP);
                }
            } else if (cache.containsKey(HierarchyGroupSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, Hierarchy.Result.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so no error - proceed
                return shim(level, cache, HierarchyGroupSymbols.EXCLUDE_ALL, entityNames);
            } else if(cache.containsKey(HierarchyGroupSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good - proceed
                return shim(level, cache, HierarchyGroupSymbols.INCLUDE_ALL_OPTIONALLY, entityNames);
            } else if (cache.containsKey(HierarchyGroupSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard its an error
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (level instanceof HierarchyGroupLevel) {
                // It cannot be null if we expect a group
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (cache.containsKey(HierarchyGroupSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            }
        }
    }

    /*
     * If the validation is not already complete and returned out of the above
     * recursive method it is terminated by this operation with a set lookup
     */
    private Pair<HierarchyLevel, Hierarchy.Result> evaluate(HierarchyLevel level, Set cache, Map<HierarchyLevel, String> entityNames) {
        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.contains(HierarchySymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Hierarchy.Result.EXCLUDED);
            } else if (cache.contains(HierarchyGroupSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Hierarchy.Result.NOT_EXPECTED);
            } else if (cache.contains(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return Pair.of(level, Hierarchy.Result.OK);
            } else if(cache.contains(HierarchyGroupSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (cache.contains(HierarchyGroupSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (level instanceof HierarchyGroupLevel) {
                if (GroupCommon.cacheContainsGroupContainsName((HierarchyGroupLevel)level, cache, entityNames.get(level))) {
                    return Pair.of(level, Hierarchy.Result.OK);
                } else {
                    return Pair.of(level, Hierarchy.Result.NOT_IN_GROUP);
                }
            } else if (cache.contains(HierarchyGroupSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else {
                // We have not found the item
                return Pair.of(level, Hierarchy.Result.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.contains(HierarchySymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so no error - ok
                return Pair.of(level, Hierarchy.Result.OK);
            } else if(cache.contains(HierarchyGroupSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (cache.contains(HierarchyGroupSymbols.INCLUDE_ALL)) {
                // if the item is on a plain wildcard its an error
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (level instanceof HierarchyGroupLevel) {
                // It cannot be null if we expect a group
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (cache.contains(HierarchyGroupSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something.
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            }
        }
    }

    /*
     * Helper function to direct to the map or set evaluator - the hierarchy is terminated by a set
     * So the system has been set up so that initial cache is
     * Map<String, Map<String, Map<String, Set<String>>>>
     * Then as each entity is validated the cache is drilled into so that we get the following
     * sequence
     *
     * Map<String, Map<String, Map<String, Set<String>>>> - Cache by Return type
     * Map<String, Map<String, Set<String>>> - cache by releases and transfers
     * Map<String, Set<String>> - cache by parameters
     * Set<String> - a hash-set of units
     */
    private Pair<HierarchyLevel, Hierarchy.Result> shim(HierarchyLevel level, Map cache, String cacheKey, Map<HierarchyLevel, String> entityNames) {
        entityNames.remove(level);
        if (cache.get(cacheKey) instanceof Set) {
            return evaluate(hierarchyNodesIttr.next(), (Set)cache.get(cacheKey), entityNames);
        } else {
            return evaluate(hierarchyNodesIttr.next(), (Map)cache.get(cacheKey), entityNames);
        }
    }

}
