package uk.gov.ea.datareturns.domain.jpa.dao.hierarchies;

import uk.gov.ea.datareturns.domain.jpa.dao.AliasingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Set;

/**
 * Abstract class for data access objects dao's where the entity is an aliasing entity and a hierarchy group entity
 * @param <E>
 */
public abstract class GroupingAliasingEntityDaoImpl<E extends AliasingEntity & Hierarchy.GroupedHierarchyEntity> extends AliasingEntityDao<E> implements GroupingEntityDao<E> {
    private final GroupingEntityCommon<E, ? extends AliasingEntityDao<E>> common;

    /**
     * Data access objects and caching for which the entity is both aliasing and in a hierarchy and
     * is a group level
     * @param entityClass
     */
    public GroupingAliasingEntityDaoImpl(Class<E> entityClass) {
        super(entityClass);
        this.common = new GroupingEntityCommon<>(this);
    }

    public Set<String> listGroups() {
        return common.listGroups();
    }

    public boolean isGroupMember(String group, String item) {
        return common.isGroupMember(group, item);
    }
}
