package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import java.util.Map;
import java.util.Set;

/**
 * Interface describing the methods required for hierarchy validation
 * @author Graham Willis
 */
public interface Validator {
    Pair<HierarchyLevel<? extends MasterDataEntity>, Hierarchy.Result> validate(
            Map<String, ?> cache, Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels,
            Map<HierarchyLevel<? extends MasterDataEntity>, String> entityNames);
}
