package uk.gov.ea.datareturns.domain.jpa.entities.hierarchy;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyValidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class describing the hierarchical relationships between a set of entities used for the validation
 * of input data and providing an interface for navigating
 */

public class Hierarchy<C extends CacheProvider> {
    private HierarchyNode[] hierarchyNodes = null;
    private int hierarchyLevel = 0;
    private C cacheProvider = null;

    /**
     * Initialize the hierarchy with the entity nodes
     * @param hierarchyNodes
     */
    Hierarchy(HierarchyNode[] hierarchyNodes, C cacheProvider) {
        this.hierarchyNodes = hierarchyNodes;
        this.cacheProvider = cacheProvider;
    }

    public HierarchyNode root() {
        hierarchyLevel = 0;
        return hierarchyNodes[hierarchyLevel];
    }

    public HierarchyNode next() {
        return hierarchyNodes[++hierarchyLevel];
    }

    /*
     * Public interface used by the entities participating in the hierarchy
     */
    public interface HierarchyEntity extends ControlledListEntity {
        Class<? extends EntityDao> getEntityDao();
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

    public Pair<ControlledListsList, DependencyValidation.Result> validate(HierarchyEntity... entities) {
        return null;
    }

    Map<HierarchyNode, String> processInputs(HierarchyEntity... entities) {
        Map<HierarchyNode, String> result = new HashMap<>();
        for (HierarchyEntity entity : entities) {
            //Class<? extends EntityDao> daoClass = entity.getEntityDao();
            //EntityDao daoBean = SpringApplicationContextProvider.getApplicationContext().getBean(daoClass);
            //daoBean.getByName()
        }
        return result;
    }
}

