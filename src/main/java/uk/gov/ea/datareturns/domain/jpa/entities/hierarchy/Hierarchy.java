package uk.gov.ea.datareturns.domain.jpa.entities.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

/**
 * A class describing the hierarchical relationships between a set of entities used for the validation
 * of input data and providing an interface for navigating
 */
public class Hierarchy {
    HierarchyNode[] hierarchyNodes;

    /**
     * Initialize the hierarchy with the entity nodes
     * @param hierarchyNodes
     */
    public Hierarchy(HierarchyNode[] hierarchyNodes) {
        this.hierarchyNodes = hierarchyNodes;
    }

    /*
     * Public interface used by the entities participating in the hierarchy
     */
    /**
     * Created by graham on 11/10/16.
     */
    public static interface HierarchyEntity extends ControlledListEntity {
    }

    /**
     * A wrapper function for the hierarchy entity which will also allow
     * for hierarchy meta-data to be held apart from the entity object
     */
    public class HierarchyNode {
        private int hierarchyLevel = 0;
        Class<? extends HierarchyEntity> hierarchyEntity;

        public HierarchyNode(Class<? extends HierarchyEntity> hierarchyEntity) {
            this.hierarchyEntity = hierarchyEntity;
        }

        public HierarchyNode root() {
            hierarchyLevel = 0;
            return hierarchyNodes[hierarchyLevel];
        }

        public HierarchyNode next() {
            return hierarchyNodes[++hierarchyLevel];
        }
    }

}
