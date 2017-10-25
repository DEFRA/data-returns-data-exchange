package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface describing the methods required for hierarchy navigation
 * @author Graham Willis
 */
public interface Navigator {
    Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Map<String, ?> cache,
            Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> hierarchyNodeStringMap);

    Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Map<String, ?> cache, Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> hierarchyNodeStringMap,
            String field, String contains);

}
