package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import java.util.Map;
import java.util.Set;

/**
 * Created by graham on 21/11/16.
 */
public interface Validator {
    Pair<HierarchyLevel, Hierarchy.Result> validate(Map cache, Set<HierarchyLevel> hierarchyLevels, Map<HierarchyLevel, String> entityNames);
}
