package uk.gov.ea.datareturns.domain.jpa.dao;

import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;

import java.util.Set;

/**
 * DAO for monitoring methods and standards.
 *
 * @author Graham Willis
 */
public interface GroupingEntityDao<E extends ControlledListEntity> {
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
    Set<E> getGroupMembers(String group);

    /**
     * Test if a given entity is a member of a group
     * @param group The group
     * @param item The entity
     * @return true if the entity is a member of the group
     */
    boolean isGroupMember(String group, String item);
}
