package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;

/**
 * Represents a level in the hierarchy where grouping functions may be used. Groups are indicated by
 * their enclosure in square brackets []. If a group is indicated at a level in the hierarchy
 * then all entities what are members of the group will validate and will be listed
 * @author Graham Willis
 */
public class HierarchyGroupLevel<E extends Hierarchy.GroupedHierarchyEntity> extends HierarchyLevel<E> {
    public HierarchyGroupLevel(Class<E> hierarchyEntity,
                               Class<? extends EntityDao<E>> daoClass,
                               ControlledListsList controlledList) {

        super(hierarchyEntity, daoClass, controlledList);
    }
}
