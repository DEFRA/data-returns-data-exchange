package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.Hierarchy;

import javax.inject.Inject;
import java.util.*;

/**
 * Navigate through the tree of mutual dependencies between lists
 * Return Type -> Releases and transfers (For PI) -> Parameters -> Units
 * Allows the resulting filtered list to be returned at any level
 * The dependencies are encoded in the in Dependencies.csv file
 */
@Component
public class DependencyNavigation implements ApplicationContextAware  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DependencyNavigation.class);

    private ApplicationContext applicationContext;
    private ParameterDao parameterDao;
    private ReturnTypeDao returnTypeDao;
    private ReleasesAndTransfersDao releasesAndTransfersDao;
    private UnitDao unitDao;
    private DependenciesDao dao;
    private DependencyValidation dependencyValidation;

    @Inject
    public DependencyNavigation(ParameterDao parameterDao, ReturnTypeDao returnTypeDao,
                                ReleasesAndTransfersDao releasesAndTransfersDao,
                                UnitDao unitDao, DependenciesDao dao,
                                DependencyValidation dependencyValidation) {

        this.parameterDao = parameterDao;
        this.returnTypeDao = returnTypeDao;
        this.releasesAndTransfersDao = releasesAndTransfersDao;
        this.unitDao = unitDao;
        this.dao = dao;
        this.dependencyValidation = dependencyValidation;
    }

    /*
     * These other signatures are for convenience
     */
    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> traverseHierarchy(ReturnType returnType) {
        return traverseHierarchy(returnType, null, null, null);
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> traverseHierarchy(ReturnType returnType, Parameter parameter) {
        return traverseHierarchy(returnType, null, parameter, null);
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> traverseHierarchy(ReturnType returnType, ReleasesAndTransfers releasesAndTransfers) {
        return traverseHierarchy(returnType, releasesAndTransfers, null, null);
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> traverseHierarchy(ReturnType returnType, ReleasesAndTransfers releasesAndTransfers, Parameter parameter) {
        return traverseHierarchy(returnType, releasesAndTransfers, parameter, null);
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> traverseHierarchy(ReturnType returnType,
                                                                                                  ReleasesAndTransfers releasesAndTransfers,
                                                                                                  Parameter parameter,
                                                                                                  Unit unit) {

        // We need to traverse the cache using the components we are given and substituting
        // wildcards and processing exclusions where necessary
        Map<String, Map<String, Map<String, Set<String>>>> cache = dao.getCache();
        // Figure out what we have been given
        String returnTypeName = returnType == null ? null : returnTypeDao.getKeyFromRelaxedName(returnType.getName());
        String releasesAndTransfersName = releasesAndTransfers == null ? null : releasesAndTransfersDao.getKeyFromRelaxedName(releasesAndTransfers.getName());
        String parameterName = parameter == null ? null : parameterDao.getKeyFromRelaxedName(parameter.getName());
        String unitName = unit == null ? null : unitDao.getKeyFromRelaxedName(unit.getName());

        // Put the names in a list
        Map<ControlledListsList, String> entityNames = new HashMap();

        entityNames.put(ControlledListsList.RETURN_TYPE, returnTypeName);
        entityNames.put(ControlledListsList.RELEASES_AND_TRANSFERS, releasesAndTransfersName);
        entityNames.put(ControlledListsList.PARAMETERS, parameterName);
        entityNames.put(ControlledListsList.UNITS, unitName);

        return doTraverse(ControlledListsList.RETURN_TYPE, cache, entityNames, new HashMap<>());
    }

    private Pair<ControlledListsList,List<? extends Hierarchy.HierarchyEntity>> shim(ControlledListsList level, Map cache, String cacheKey, Map<ControlledListsList, String> entityNames, Map<ControlledListsList, String> keys) {
        // Move the key between the traversing array to the traversed array
        keys.put(level, entityNames.remove(level));
        if (cache.get(cacheKey) instanceof Set) {
            return getFilteredList(level.next(), (Set)cache.get(cacheKey));
        } else {
            return doTraverse(level.next(), (Map)cache.get(cacheKey), entityNames, keys);
        }
    }

    private Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> doTraverse(ControlledListsList level, Map cache, Map<ControlledListsList, String> entityNames, Map<ControlledListsList, String> keys) {
        if (entityNames.get(level) != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(DependencyValidationSymbols.EXCLUDE + entityNames.get(level))) {
                // If we have supplied an explicitly excluded item then return null
                return Pair.of(level, null);
            } else if (cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so carry error
                return Pair.of(level, null);
            } else if (cache.containsKey(entityNames.get(level))) {
                // Item explicitly listed - Proceed
                return shim(level, cache, entityNames.get(level), entityNames, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(level, cache, DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY, entityNames, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(level, cache, DependencyValidationSymbols.INCLUDE_ALL, entityNames, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We should not be here
                return Pair.of(level, null);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, null);
            }
        } else if (entityNames.get(level) == null && cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
            return shim(level, cache, DependencyValidationSymbols.EXCLUDE_ALL, entityNames, keys);
        } else if (allNulls(entityNames)) {
            return getFilteredList(level, cache);
        } else {
            return Pair.of(level, null);
        }
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> getFilteredList(ControlledListsList level, Map cache) {
        // Get the Dao from the level
        Class<? extends EntityDao> listItemDaoClass = level.getDao();

        // Get the DAO from spring
        EntityDao<? extends Hierarchy.HierarchyEntity> listItemDao = applicationContext.getBean(listItemDaoClass);
        List<? extends Hierarchy.HierarchyEntity> itemList = listItemDao.list();
        List<Hierarchy.HierarchyEntity> resultList = new ArrayList();

        if (cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.containsKey(DependencyValidationSymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            for (Hierarchy.HierarchyEntity e : itemList) {
                String name = listItemDao.getKeyFromRelaxedName(e.getName());
                if (cache.containsKey(DependencyValidationSymbols.EXCLUDE + name)) {
                    // Explicitly excluded - do nothing
                } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL)) {
                    // Item included via wildcard
                    resultList.add(e);
                } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                    // Item included via optional wildcard
                    resultList.add(e);
                } else if (cache.containsKey(name)) {
                    // Item included by name
                    resultList.add(e);
                }
            }
        }
        return Pair.of(level, resultList);
    }

    public Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> getFilteredList(ControlledListsList level, Set cache) {
        // Get the Dao from the level
        Class<? extends EntityDao> listItemDaoClass = level.getDao();

        // Get the DAO from spring
        EntityDao<? extends Hierarchy.HierarchyEntity> listItemDao = applicationContext.getBean(listItemDaoClass);
        List<? extends Hierarchy.HierarchyEntity> itemList = listItemDao.list();
        List<Hierarchy.HierarchyEntity> resultList = new ArrayList();

        if (cache.contains(DependencyValidationSymbols.EXCLUDE_ALL)) {
            // If we have the inverse wildcard we are not expecting anything item which is an error
            return Pair.of(level, null);
        } else if (cache.contains(DependencyValidationSymbols.NOT_APPLICABLE)) {
            // No applicable output
            return Pair.of(level, null);
        } else {
            // Test the items individually with the cache.
            // I have split out the branching for clarity
            for (Hierarchy.HierarchyEntity e : itemList) {
                String name = listItemDao.getKeyFromRelaxedName(e.getName());
                if (cache.contains(DependencyValidationSymbols.EXCLUDE + name)) {
                    // Explicitly excluded - do nothing
                } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL)) {
                    // Item included via wildcard
                    resultList.add(e);
                } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                    // Item included via optional wildcard
                    resultList.add(e);
                } else if (cache.contains(name)) {
                    // Item included by name
                    resultList.add(e);
                }
            }
        }
        return Pair.of(level, resultList);
    }

    /*
     * Returns a the list of entities at at the child level to the one being specified skipping
     * any levels that are not specified
     *
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean allNulls(Map m) {
        Set ks = m.keySet();
        for (Object key: ks) {
            if (m.get(key) != null) {
                return false;
            }
        }
        return true;
    }
}
