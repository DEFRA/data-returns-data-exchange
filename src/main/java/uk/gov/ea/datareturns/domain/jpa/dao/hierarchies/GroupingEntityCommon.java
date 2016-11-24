package uk.gov.ea.datareturns.domain.jpa.dao.hierarchies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Functionality for classes implementing GroupingEntityDao - collects the functions in a single place
 * @param <E>
 * @param <F>
 */
class GroupingEntityCommon<E extends Hierarchy.GroupedHierarchyEntity, F extends EntityDao<E>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupingEntityCommon.class);
    private final F dao;
    private volatile Map<String, Set<E>> cacheByGroup = null;

    public GroupingEntityCommon(F dao) {
        this.dao = dao;
    }

    public Map<String, Set<E>> getCacheByGroup() {
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
        return getCacheByGroup().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public boolean isGroupMember(String group, String item) {
        E e = dao.getByNameRelaxed(item);
        return !(e == null || e.getGroup() == null) && dao.getKeyFromRelaxedName(e.getGroup()).equals(group);
    }
}
