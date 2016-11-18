package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 * A class describing the hierarchical relationships between a set of entities used for the validation
 * of input data and providing an interface for navigating
 */
public class Hierarchy<C extends CacheProvider> {
    private final Set<HierarchyNode> hierarchyNodes;
    private final C cacheProvider;
    private final HierarchyNavigator hierarchyNavigator;
    private final HierarchyValidator hierarchyValidator;
    Map<Class<? extends HierarchyEntity>, HierarchyNode> hierarchyNodesByHierarchyEntity;

    /**
     * Initialize the hierarchy with the entity nodes and a cache provider
     * @param hierarchyNodes
     * @param hierarchyNavigator
     * @param hierarchyValidator
     */
    Hierarchy(Set<HierarchyNode> hierarchyNodes, C cacheProvider, HierarchyNavigator hierarchyNavigator, HierarchyValidator hierarchyValidator) {
        this.hierarchyNodes = hierarchyNodes;
        this.cacheProvider = cacheProvider;
        this.hierarchyNavigator = hierarchyNavigator;
        this.hierarchyValidator = hierarchyValidator;
        this.hierarchyNodesByHierarchyEntity = hierarchyNodes
                .stream()
                .collect(Collectors.toMap(HierarchyNode::getHierarchyEntityClass, v -> v));
    }

    /*
     * Public interface used by the entities participating in the hierarchy
     */
    public interface HierarchyEntity extends ControlledListEntity {
    }

    /**
     * The result of the validation. Excluded entities are distinguished from
     * not found. From an end user perspective the two things are the same.
     * EXPECTED indicates that a required entity has not been supplied and
     * NOT_EXPECTED indicates that an entity which is explicitly not required was supplied
     */
    public enum Result {
        OK, EXPECTED, NOT_EXPECTED, NOT_FOUND, EXCLUDED
    }

    /**
     * List the children of a given set of parents. The parents must form a complete and proper path
     * to be able to calculate the hierarchy
     */
    public Pair<HierarchyNode, List<? extends HierarchyEntity>> getChildren(HierarchyEntity... entities) {
        return null;
    }

    public Pair<HierarchyNode, Hierarchy.Result> validate(HierarchyEntity... entities) {
        return hierarchyValidator.validate((Map)cacheProvider.getCache(), hierarchyNodes, processInputs(entities));
    }

    /**
     * Sets the string value on each node
     * @param entities
     */
    private Map<HierarchyNode, String> processInputs(HierarchyEntity... entities) {
        Map<HierarchyNode, String> result = new HashMap<>();
        for (HierarchyNode hierarchyNode : hierarchyNodes) {
            result.put(hierarchyNode, null);
            Class<? extends Hierarchy.HierarchyEntity> hierarchyEntityClass = hierarchyNode.getHierarchyEntityClass();
            for (HierarchyEntity entity : entities) {
                if (entity != null && entity.getClass().equals(hierarchyEntityClass)) {
                    EntityDao dao = EntityDao.getDao(hierarchyNode.getDaoClass());
                    result.put(hierarchyNode, dao.getKeyFromRelaxedName(entity.getName()));
                }
            }
        }
        return result;
    }
}

