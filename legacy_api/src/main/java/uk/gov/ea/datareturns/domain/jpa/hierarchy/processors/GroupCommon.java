package uk.gov.ea.datareturns.domain.jpa.hierarchy.processors;

import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.JpaRepositoryConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupSymbols;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.MasterDataRepository;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataNaturalKeyService;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Group functions for entities extending Hierarchy.GroupedHierarchyEntity
 * @author Graham Willis
 */
@Component
public class GroupCommon {
    private final JpaRepositoryConfiguration repositoryConfiguration;
    private final MasterDataNaturalKeyService keyService;
    private final MasterDataLookupService lookupService;

    @Inject
    public GroupCommon(JpaRepositoryConfiguration repositoryConfiguration, MasterDataNaturalKeyService keyService,
            MasterDataLookupService lookupService) {
        this.repositoryConfiguration = repositoryConfiguration;
        this.keyService = keyService;
        this.lookupService = lookupService;
    }

    public <E extends MasterDataEntity> boolean cacheContainsGroupContainsName(HierarchyGroupLevel<E> level, Map<String, ?> cache,
            String entityName) {
        Set<String> cacheSet = cache.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return cacheContainsGroupContainsName(level, cacheSet, entityName);
    }

    /**
     * For grouped levels we need to test if the cache contains a group and for which the
     * entity name is a member of the group
     * @param level The grouped hierarchy level
     * @param cache The cache
     * @param entityName The given enity name
     * @return true if entity name is found
     */
    public <E extends MasterDataEntity> boolean cacheContainsGroupContainsName(HierarchyGroupLevel<E> level, Set<String> cache,
            String entityName) {
        return getGroupInCacheFromName(level, cache, entityName) != null;
    }

    /**
     * Find the group in the cache that corresponds to the supplied entity name
     * @param level The hierarchy level
     * @param cache The cache
     * @param entityName The entity name
     * @return The group
     */
    public <E extends MasterDataEntity> String getGroupInCacheFromName(HierarchyGroupLevel<E> level, Set<String> cache,
            String entityName) {
        Set<String> groups = findGroups(cache, level);
        for (String group : groups) {
            if (isGroupMember(level, group, entityName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Find the groups on the hierarchy level
     * @param cache The cache
     * @return A set of groups
     */
    public <E extends MasterDataEntity> Set<String> findGroups(Set<String> cache, HierarchyGroupLevel<E> level) {
        MasterDataRepository<E> repo = repositoryConfiguration.getMasterDataRepository(level.getHierarchyEntityClass());

        StringPath groupFieldPath = level.getGroupFieldPath();

        Collection<E> data = IterableUtils.toList(repo.findAll(groupFieldPath.isNotNull()));
        Map<String, Set<E>> dataByGroupField = data.stream()
                .collect(Collectors.groupingBy(u -> getGroupFieldValue(u, level.getGroupFieldGetter()), Collectors.toSet()));

        // Get the list of groups associated with the entity at this level
        Set<String> levelGroups = dataByGroupField.keySet();

        // Get the list of groups at this level in the cache
        Set<String> cacheGroups = cache.stream()
                .filter(HierarchyGroupSymbols::isGroup)
                .map(HierarchyGroupSymbols::extractGroup)
                .collect(Collectors.toSet());

        // Get the groups appearing in both sets.
        Set<String> groups = new HashSet<>(levelGroups);
        groups.retainAll(cacheGroups);

        return groups;
    }

    private String getGroupFieldValue(Object entity, Method getterMethod) {
        try {
            if (entity != null && getterMethod != null) {
                return toGroupName(Objects.toString(getterMethod.invoke(entity), null));
            }
            return null;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private <E extends MasterDataEntity> boolean isGroupMember(HierarchyGroupLevel<E> level, String targetGroup,
            String entityName) {
        // Lookup the entity for the given name
        E entity = lookupService.relaxed().find(level.getHierarchyEntityClass(), entityName);
        // Check the entity group matches our expectations
        String entityGroupValue = getGroupFieldValue(entity, level.getGroupFieldGetter());
        return StringUtils.equalsIgnoreCase(StringUtils.trim(targetGroup), StringUtils.trim(entityGroupValue));
    }

    public String getGroupInCacheFromName(HierarchyGroupLevel<?> level, Map<String, ?> cache, String entityName) {
        Set<String> cacheSet = cache.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return getGroupInCacheFromName(level, cacheSet, entityName);
    }

    public String toGroupName(String groupFieldValue) {
        return StringUtils.upperCase(StringUtils.trimToNull(groupFieldValue));
    }

    public boolean allNulls(Map<?, ?> m) {
        Set ks = m.keySet();
        for (Object key : ks) {
            if (m.get(key) != null) {
                return false;
            }
        }
        return true;
    }
}
