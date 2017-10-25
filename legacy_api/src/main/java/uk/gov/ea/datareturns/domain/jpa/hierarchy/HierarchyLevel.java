package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;

/**
 * Represents a level in a hierarchy
 * Essentially a wrapper function for the hierarchy entity which will also allow
 * for hierarchy meta-data to be held apart from the entity object
 * @author Graham Willis
 */
public class HierarchyLevel<E extends MasterDataEntity> {
    private final ControlledListsList controlledList;
    private final Class<E> hierarchyEntityClass;

    public HierarchyLevel(Class<E> hierarchyEntity,
            ControlledListsList controlledList) {

        this.hierarchyEntityClass = hierarchyEntity;
        this.controlledList = controlledList;
    }

    public Class<E> getHierarchyEntityClass() {
        return hierarchyEntityClass;
    }

    public ControlledListsList getControlledList() {
        return controlledList;
    }
}

