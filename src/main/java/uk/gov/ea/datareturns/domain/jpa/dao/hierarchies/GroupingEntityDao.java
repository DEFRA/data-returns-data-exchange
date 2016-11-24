package uk.gov.ea.datareturns.domain.jpa.dao.hierarchies;

import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Set;

/**
 * @Author Graham Willis
 * Interface for the Dao which use groups on the hierarchy. Because Java does not support multiple
 * inheritance it is necessary to have two implementing subclasses one for aliasing entities
 * and one for not aliasing entities.
 */
public interface GroupingEntityDao<E extends Hierarchy.GroupedHierarchyEntity> {
    /**
     * List the set of groups allowed for this entity
     * @return
     */
    Set<String> listGroups();

    /**
     * List the members of a given group
     * @param group The group
     * @return
     */
    //Set<E> getGroupMembers(String group);

    /**
     * Test if a given entity is a member of a group
     * @param group The group
     * @param item The entity
     * @return true if the entity is a member of the group
     */
    boolean isGroupMember(String group, String item);

 }
