package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.AliasingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.util.CachingSupplier;

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
    private CachingSupplier<Map<String, Set<E>>> cacheByGroup = CachingSupplier.of(this::cacheSupplier);

    private Map<String, Set<E>> getCacheByGroup() {
        return cacheByGroup.get();
    }

    public Set<String> listGroups() {
        return getCacheByGroup().keySet();
    }

    public boolean isGroupMember(String group, String item) {
        E e;
        // TODO: This needs rethinking
        if (dao instanceof AliasingEntityDao) {
            e = (E) ((AliasingEntityDao) dao).getByNameOrAlias(Key.relaxed(item));
        } else {
            e = dao.getByName(Key.relaxed(item));
        }

        return !(e == null || e.getGroup() == null) && dao.generateMash(e.getGroup()).equals(group);
    }

    @SuppressWarnings("unchecked")
    public void setDao(EntityDao dao) {
        this.dao = dao;
    }

    /**
     * Method to populate the cache.  This is invoked lazily when the cache needs to be built or is explicitly cleared/rebuilt.
     * @return a {@link Map} of cache data
     */
    private Map<String, Set<E>> cacheSupplier() {
        LOGGER.info("Build cache by group of: " + dao.getEntityClass().getSimpleName());
        return dao.list().stream()
                .filter(p -> p.getGroup() != null)
                .collect(Collectors.groupingBy(u -> dao.generateMash(u.getGroup()), Collectors.toSet()));
    }
}