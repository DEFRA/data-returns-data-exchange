package uk.gov.ea.datareturns.domain.jpa.dao.hierarchies;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Map;
import java.util.Set;

/**
 * @author Graham Willis
 * Abstract class for data access objects dao's where the entity is an aliasing entity and a hierarchy group entity
 */
public abstract class GroupingEntityDaoImpl<E extends Hierarchy.GroupedHierarchyEntity> extends EntityDao<E> implements GroupingEntityDao<E> {
    private final GroupingEntityCommon<E, ? extends EntityDao<E>> common;
    /**
     * Data access objects and caching for which the entity is both aliasing and in a hierarchy
     * with a group level
     * @param entityClass
     */
    public GroupingEntityDaoImpl(Class<E> entityClass) {
        super(entityClass);
        this.common = new GroupingEntityCommon<>(this);
    }

    private Map<String, Set<E>> getCacheByGroup() {
        return common.getCacheByGroup();
    }

    public Set<String> listGroups() {
        return common.listGroups();
    }

    public boolean isGroupMember(String group, String item) {
        return common.isGroupMember(group, item);
    }
}
