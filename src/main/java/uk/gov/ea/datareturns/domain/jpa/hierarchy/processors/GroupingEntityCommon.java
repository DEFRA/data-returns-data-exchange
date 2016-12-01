package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Functionality for entities implementing GroupedHierarchyEntity
 * @author Graham Willis
 *
 * @param <E> The entity
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupingEntityCommon<E extends Hierarchy.GroupedHierarchyEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupingEntityCommon.class);
    private EntityDao<E> dao = null;
    private volatile Map<String, Set<E>> cacheByGroup = null;

    private Map<String, Set<E>> getCacheByGroup() {
        if (cacheByGroup == null) {
            synchronized (this) {
                if (cacheByGroup == null) {
                    LOGGER.info("Build cache by group of: " + dao.entityClass.getSimpleName());
                    cacheByGroup = dao.list()
                            .stream()
                            .filter(p -> p.getGroup() != null)
                            .collect(Collectors.groupingBy(u -> dao.getKeyFromRelaxedName(u.getGroup()), Collectors.toSet()));
                }
            }
        }
        return cacheByGroup;
    }

    public Set<String> listGroups() {
        return getCacheByGroup().keySet();
    }

    public boolean isGroupMember(String group, String item) {
        E e = dao.getByNameRelaxed(item);
        return !(e == null || e.getGroup() == null) && dao.getKeyFromRelaxedName(e.getGroup()).equals(group);
    }

    @SuppressWarnings("unchecked")
    public void setDao(EntityDao dao) {
        this.dao = dao;
    }
}
