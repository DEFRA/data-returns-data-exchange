package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchySymbols;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Graham Willis
 * Validate a set of entities against a specific hierarchy the result of the validation
 * and the lowest level in the hierarch reached to obtain the result
 */
@Component
public class SimpleValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleValidator.class);

    public Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> validate(
            Map<String, ?>  cache, Set<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames) {

        Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr = hierarchyLevels.iterator();
        HierarchyLevel<? extends Hierarchy.HierarchyEntity> rootNode = hierarchyNodesIttr.next();
        return evaluate(hierarchyNodesIttr, rootNode, cache, entityNames);
    }

    /*
     * Main evaluating function which is recursive as the rules are the same for each entity
     */
    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> evaluate(
            Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr,
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Map<String, ?> cache, Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames) {

        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(HierarchySymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Hierarchy.Result.EXCLUDED);
            } else if (cache.containsKey(HierarchySymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Hierarchy.Result.NOT_EXPECTED);
            } else if (cache.containsKey(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return shim(hierarchyNodesIttr, level, cache, entityNames.get(level), entityNames);
            } else if(cache.containsKey(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.INCLUDE_ALL_OPTIONALLY, entityNames);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.INCLUDE_ALL, entityNames);
            } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
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
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.EXCLUDE_ALL, entityNames);
            } else if(cache.containsKey(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good - proceed
                return shim(hierarchyNodesIttr, level, cache, HierarchySymbols.INCLUDE_ALL_OPTIONALLY, entityNames);
            } else if (cache.containsKey(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard its an error
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (cache.containsKey(HierarchySymbols.NOT_APPLICABLE)) {
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
    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> evaluate(
            Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr,
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Set<String> cache,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames) {

        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.contains(HierarchySymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Hierarchy.Result.EXCLUDED);
            } else if (cache.contains(HierarchySymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Hierarchy.Result.NOT_EXPECTED);
            } else if (cache.contains(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return Pair.of(level, Hierarchy.Result.OK);
            } else if(cache.contains(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (cache.contains(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - OK
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (cache.contains(HierarchySymbols.NOT_APPLICABLE)) {
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
            } else if(cache.contains(HierarchySymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good
                return Pair.of(level, Hierarchy.Result.OK);
            } else if (cache.contains(HierarchySymbols.INCLUDE_ALL)) {
                // if the item is on a plain wildcard its an error
                return Pair.of(level, Hierarchy.Result.EXPECTED);
            } else if (cache.contains(HierarchySymbols.NOT_APPLICABLE)) {
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
     */
    private Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> shim(
            Iterator<HierarchyLevel<? extends Hierarchy.HierarchyEntity>> hierarchyNodesIttr,
            HierarchyLevel<? extends Hierarchy.HierarchyEntity> level,
            Map<String, ?>  cache, String cacheKey,
            Map<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, String> entityNames) {

        if (!hierarchyNodesIttr.hasNext()) {
            LOGGER.error("Error evaluating hierarchy at : " + level.getControlledList().getDescription());
            return Pair.of(level, Hierarchy.Result.NOT_FOUND);
        }

        entityNames.remove(level);

        if (cache.get(cacheKey) instanceof Set) {
            return evaluate(hierarchyNodesIttr, hierarchyNodesIttr.next(), (Set<String>)cache.get(cacheKey), entityNames);
        } else {
            return evaluate(hierarchyNodesIttr, hierarchyNodesIttr.next(), (Map<String, ?>)cache.get(cacheKey), entityNames);
        }
    }
}
