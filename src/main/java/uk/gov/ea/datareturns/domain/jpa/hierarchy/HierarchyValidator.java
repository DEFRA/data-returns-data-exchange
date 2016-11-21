package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Set;

/**
 * Created by graham on 21/11/16.
 */
public interface HierarchyValidator {
    Pair<HierarchyLevel, Hierarchy.Result> validate(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> entityNames);
}
