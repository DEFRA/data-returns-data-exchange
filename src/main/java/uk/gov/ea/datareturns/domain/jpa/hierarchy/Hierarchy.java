package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.Navigator;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.Validator;

import java.util.*;

/**
 * @author Graham Willis
 * A class describing the hierarchical relationships between a set of entities used for the validation
 * of input data and providing an interface for navigating
 */
public class Hierarchy<C extends HierarchyCacheProvider> {
    private final Set<HierarchyLevel> hierarchyLevels;
    private final C cacheProvider;
    private final Navigator hierarchyNavigator;
    private final Validator hierarchyValidator;

    /**
     * Initialize the hierarchy with the entity nodes and a cache provider
     * @param hierarchyLevels
     * @param hierarchyNavigator
     * @param hierarchyValidator
     */
    public Hierarchy(Set<HierarchyLevel> hierarchyLevels, C cacheProvider, Navigator hierarchyNavigator, Validator hierarchyValidator) {
        this.hierarchyLevels = hierarchyLevels;
        this.cacheProvider = cacheProvider;
        this.hierarchyNavigator = hierarchyNavigator;
        this.hierarchyValidator = hierarchyValidator;
    }

    /*
     * Public interface used by the entities participating in the hierarchy
     */
    public interface HierarchyEntity extends ControlledListEntity {
    }

    /*
     * Public interface used by the entities participating in the hierarchy
     */
    public interface GroupedHierarchyEntity extends HierarchyEntity {
        String getGroup();
    }

    /**
     * The result of the validation. Excluded entities are distinguished from
     * not found. From an end user perspective the two things are the same.
     * EXPECTED indicates that a required entity has not been supplied and
     * NOT_EXPECTED indicates that an entity which is explicitly not required was supplied
     */
    public enum Result {
        OK, EXPECTED, NOT_EXPECTED, NOT_FOUND, NOT_IN_GROUP, EXCLUDED
    }

    /**
     * List the children of a given set of parents. The parents must form a complete and proper path
     * to be able to calculate the hierarchy
     */
    public Pair<HierarchyLevel, List<? extends HierarchyEntity>> children(Set<HierarchyEntity> entities) {
        return hierarchyNavigator.children((Map)cacheProvider.getCache(), hierarchyLevels, processInputs(entities));
    }

    /**
     * List the children of a given set of parents. The parents must form a complete and proper path
     * to be able to calculate the hierarchy. This overdide causes the list to be filtered
     */
    public Pair<HierarchyLevel, List<? extends HierarchyEntity>> children(Set<HierarchyEntity> entities, String field, String contains) {
        return hierarchyNavigator.children((Map)cacheProvider.getCache(), hierarchyLevels, processInputs(entities), field, contains);
    }

    /**
     * Helper - especially for unit tests
     */
    public Pair<HierarchyLevel, List<? extends HierarchyEntity>> children(HierarchyEntity... entities) {
        return hierarchyNavigator.children((Map)cacheProvider.getCache(), hierarchyLevels, processInputs(new HashSet<>(Arrays.asList(entities))));
    }

    /**
     * Validate that a given set of entities is a member of the hierarchy
     * @param entities
     * @return
     */
    public Pair<HierarchyLevel, Hierarchy.Result> validate(Set<HierarchyEntity> entities) {
        return hierarchyValidator.validate((Map)cacheProvider.getCache(), hierarchyLevels, processInputs(entities));
    }

    /**
     * Helper - especially for unit tests
     */
    public Pair<HierarchyLevel, Hierarchy.Result> validate(HierarchyEntity... entities) {
        return hierarchyValidator.validate((Map)cacheProvider.getCache(),
                hierarchyLevels, processInputs(new HashSet<>(Arrays.asList(entities))));
    }

    /**
     * Sets the string value on each given entity - the cache key is derived by the entity relaxed name
     * @param entities
     */
    private Map<HierarchyLevel, String> processInputs(Set<HierarchyEntity> entities) {
        Map<HierarchyLevel, String> result = new HashMap<>();
        for (HierarchyLevel hierarchyLevel : hierarchyLevels) {
            result.put(hierarchyLevel, null);
            Class<? extends Hierarchy.HierarchyEntity> hierarchyEntityClass = hierarchyLevel.getHierarchyEntityClass();
            for (HierarchyEntity entity : entities) {
                if (entity != null && entity.getClass().equals(hierarchyEntityClass)) {
                    EntityDao dao = EntityDao.getDao(hierarchyLevel.getDaoClass());
                    result.put(hierarchyLevel, dao.getKeyFromRelaxedName(entity.getName()));
                }
            }
        }
        return result;
    }

    public Set<HierarchyLevel> getHierarchyLevels() {
        return hierarchyLevels;
    }
}

