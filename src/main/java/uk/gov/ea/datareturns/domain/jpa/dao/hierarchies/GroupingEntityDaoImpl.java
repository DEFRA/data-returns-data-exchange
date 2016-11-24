package uk.gov.ea.datareturns.domain.jpa.dao.hierarchies;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Map;
import java.util.Set;

/**
 * Created by graham on 24/11/16.
 */
public abstract class GroupingEntityDaoImpl<E extends Hierarchy.GroupedHierarchyEntity> extends EntityDao<E> implements GroupingEntityDao<E> {
    private GroupingEntityCommon common;
    /**
     * Data access objects and caching for which the entity is both aliasing and in a hierarchy
     * with a group level
     * @param entityClass
     */
    public GroupingEntityDaoImpl(Class<E> entityClass) {
        super(entityClass);
        this.common = new GroupingEntityCommon(this);
    }

    private Map<String, Set<E>> getCacheByGroup() {
        return common.getCacheByGroup();
    }

    public Set<String> listGroups() {
        return common.listGroups();
    }

    public Set<E> getGroupMembers(String group) {
        return common.getGroupMembers(group);
    }

    public boolean isGroupMember(String group, String item) {
        return common.isGroupMember(group, item);
    }
}
