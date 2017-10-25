package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.apache.commons.lang3.tuple.Pair;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.Navigator;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.Validator;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataNaturalKeyService;

import java.util.*;

/**
 * @author Graham Willis
 * A class describing the hierarchical relationships between a set of entities used for the validation
 * of input data and providing an interface for navigating
 */
public class Hierarchy<C extends HierarchyCacheProvider<? extends Map<String, ?>>> {
    private final MasterDataLookupService lookupService;
    private final MasterDataNaturalKeyService keyService;

    private final Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels;
    private final C cacheProvider;
    private final Navigator hierarchyNavigator;
    private final Validator hierarchyValidator;

    /**
     * Initialize the hierarchy with the entity nodes and a cache provider
     * @param hierarchyLevels The levels in the hierarchy
     * @param hierarchyNavigator The navigator to use
     * @param hierarchyValidator The validator to use
     */
    protected Hierarchy(MasterDataLookupService lookupService,
            MasterDataNaturalKeyService keyService,
            Set<HierarchyLevel<? extends MasterDataEntity>> hierarchyLevels, C cacheProvider,
            Navigator hierarchyNavigator, Validator hierarchyValidator) {
        this.lookupService = lookupService;
        this.keyService = keyService;
        this.hierarchyLevels = hierarchyLevels;
        this.cacheProvider = cacheProvider;
        this.hierarchyNavigator = hierarchyNavigator;
        this.hierarchyValidator = hierarchyValidator;
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
    public Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Set<MasterDataEntity> entities) {
        return hierarchyNavigator.children(cacheProvider.getCache(), hierarchyLevels, processInputs(entities));
    }

    /**
     * List the children of a given set of parents. The parents must form a complete and proper path
     * to be able to calculate the hierarchy. This overdide causes the list to be filtered
     */
    public Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            Set<MasterDataEntity> entities, String field, String contains) {
        return hierarchyNavigator.children(cacheProvider.getCache(), hierarchyLevels, processInputs(entities), field, contains);
    }

    /**
     * Helper - especially for unit tests
     */
    public Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> children(
            MasterDataEntity... entities) {
        return hierarchyNavigator
                .children(cacheProvider.getCache(), hierarchyLevels, processInputs(new HashSet<>(Arrays.asList(entities))));
    }

    /**
     * Validate that a given set of entities is a member of the hierarchy
     * @param entities
     * @return The validation result
     */
    public Pair<HierarchyLevel<? extends MasterDataEntity>, Hierarchy.Result> validate(Set<MasterDataEntity> entities) {
        return hierarchyValidator.validate(cacheProvider.getCache(), hierarchyLevels, processInputs(entities));
    }

    /**
     * Helper - especially for unit tests
     * @param entities
     * @return The validation result
     */
    public Pair<HierarchyLevel<? extends MasterDataEntity>, Hierarchy.Result> validate(MasterDataEntity... entities) {
        return hierarchyValidator.validate(cacheProvider.getCache(),
                hierarchyLevels, processInputs(new HashSet<>(Arrays.asList(entities))));
    }

    /**
     * Sets the string value on each given entity - the cache key is derived by the entity relaxed name
     * @param entities
     * A map of the supplied inputs by the hierarchy level. The map contains all the levels regardless
     * of whether inputs are supplied
     */
    private Map<HierarchyLevel<? extends MasterDataEntity>, String> processInputs(Set<MasterDataEntity> entities) {
        Map<HierarchyLevel<? extends MasterDataEntity>, String> result = new HashMap<>();
        for (HierarchyLevel<? extends MasterDataEntity> hierarchyLevel : hierarchyLevels) {
            result.put(hierarchyLevel, null);
            Class<? extends MasterDataEntity> hierarchyEntityClass = hierarchyLevel.getHierarchyEntityClass();
            for (MasterDataEntity entity : entities) {
                if (entity != null && entity.getClass().equals(hierarchyEntityClass)) {
                    result.put(hierarchyLevel, keyService.relaxKey(hierarchyEntityClass, entity.getName()));
                }
            }
        }
        return result;
    }

    public Set<HierarchyLevel<? extends MasterDataEntity>> getHierarchyLevels() {
        return hierarchyLevels;
    }
}

