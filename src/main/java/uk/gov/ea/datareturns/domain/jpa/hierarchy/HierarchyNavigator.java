package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by graham on 21/11/16.
 */
public interface HierarchyNavigator {
    public Pair<HierarchyLevel,List<? extends Hierarchy.HierarchyEntity>> children(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> hierarchyNodeStringMap);
}
