package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;

/**
 * Represents a level in a hierarchy
 * Essentially a wrapper function for the hierarchy entity which will also allow
 * for hierarchy meta-data to be held apart from the entity object
 * @Author Graham Willis
 */
public class HierarchyLevel<E extends ControlledListEntity> {
    private final ControlledListsList controlledList;
    private final Class<? extends Hierarchy.HierarchyEntity> hierarchyEntityClass;
    private final Class<? extends EntityDao<E>> daoClass;

    public HierarchyLevel(Class<? extends Hierarchy.HierarchyEntity> hierarchyEntity, Class<? extends EntityDao<E>> daoClass, ControlledListsList controlledList) {
        this.hierarchyEntityClass = hierarchyEntity;
        this.daoClass = daoClass;
        this.controlledList = controlledList;
    }

    public Class<? extends Hierarchy.HierarchyEntity> getHierarchyEntityClass() {
        return hierarchyEntityClass;
    }
    public ControlledListsList getControlledList() {
        return controlledList;
    }

    public Class<? extends EntityDao<E>> getDaoClass() {
        return daoClass;
    }
}

