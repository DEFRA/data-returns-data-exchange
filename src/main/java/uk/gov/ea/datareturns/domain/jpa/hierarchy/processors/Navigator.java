package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface describing the methods required for hierarchy navigation
 * @Author Graham Willis
 */
public interface Navigator {
    Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> children(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> hierarchyNodeStringMap);
    Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> children(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> hierarchyNodeStringMap, String field, String contains);
}
