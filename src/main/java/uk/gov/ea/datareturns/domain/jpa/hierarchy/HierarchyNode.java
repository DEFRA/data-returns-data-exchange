package uk.gov.ea.datareturns.domain.jpa.entities.hierarchy;

/**
 * Created by graham on 17/11/16.
 */
public class HierarchyNode {
    /**
     * A wrapper function for the hierarchy entity which will also allow
     * for hierarchy meta-data to be held apart from the entity object
     */

    private Class<? extends Hierarchy.HierarchyEntity> hierarchyEntity;

    public HierarchyNode(Class<? extends Hierarchy.HierarchyEntity> hierarchyEntity) {
        this.hierarchyEntity = hierarchyEntity;
    }

    public Class<? extends Hierarchy.HierarchyEntity> getHierarchyEntity() {
        return hierarchyEntity;
    }
}

