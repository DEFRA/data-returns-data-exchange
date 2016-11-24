package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.dao.hierarchies.GroupingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;

/**
 * Created by graham on 21/11/16.
 */
public class HierarchyGroupLevel<T extends Hierarchy.GroupedHierarchyEntity> extends HierarchyLevel  {

    public HierarchyGroupLevel(Class<T> hierarchyEntity,
                               Class<? extends GroupingEntityDao<T>> daoClass,
                               ControlledListsList controlledList) {

        super(hierarchyEntity, daoClass, controlledList);
    }

    public Class<? extends GroupingEntityDao<T>> getDaoClass() {
        return daoClass;
    }
}
