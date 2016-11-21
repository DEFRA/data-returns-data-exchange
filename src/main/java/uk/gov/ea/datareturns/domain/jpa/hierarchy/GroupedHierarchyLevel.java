package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;

/**
 * Created by graham on 21/11/16.
 */
public class GroupedHierarchyLevel<T extends Hierarchy.GroupedHierarchyEntity> extends HierarchyLevel  {

    public GroupedHierarchyLevel(Class<T> hierarchyEntity,
                          Class<? extends EntityDao<T>> daoClass,
                          ControlledListsList controlledList) {

        super(hierarchyEntity, daoClass, controlledList);
    }



}
