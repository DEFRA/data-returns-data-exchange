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

import javax.inject.Inject;
import java.util.*;

/**
 * Created by graham on 11/10/16.
 */
@Component
public class DependencyNavigation implements ApplicationContextAware  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DependencyNavigation.class);
    private ApplicationContext applicationContext;

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    @Inject
    private DependenciesDao dao;

    /*
     * These other signatures are for convenience
     */
    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(ReturnType returnType) {
        return traverseHierarchy(returnType, null, null, null);
    }

    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(ReturnType returnType, Parameter parameter) {
        return traverseHierarchy(returnType, null, parameter, null);
    }

    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(ReturnType returnType, ReleasesAndTransfers releasesAndTransfers) {
        return traverseHierarchy(returnType, releasesAndTransfers, null, null);
    }

    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(ReturnType returnType, ReleasesAndTransfers releasesAndTransfers, Parameter parameter) {
        return traverseHierarchy(returnType, releasesAndTransfers, parameter, null);
    }

    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(ReturnType returnType,
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

        return doTraverse(ControlledListsList.RETURN_TYPE, cache,
                new String[] {returnTypeName, releasesAndTransfersName, parameterName, unitName}, new ArrayList<String>()
        );
    }

    private Pair<ControlledListsList,List<DependentEntity>> shim(ControlledListsList level, Map cache, String cacheKey, String[] entityName, List<String> keys) {
        keys.add(entityName[0]);
        if (cache.get(cacheKey) instanceof Set) {
            return doTraverse(level.next(), (Set)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length), keys);
        } else {
            return doTraverse(level.next(), (Map)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length), keys);
        }
    }

    private Pair<ControlledListsList,List<DependentEntity>> doTraverse(ControlledListsList level, Map cache, String[] entityName, List<String> keys) {
        if (entityName.length == 0) {
            /*
             * We have reached the end of the supplied entities and so we can list the data
             */
            return getFilteredList();
        } else if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(DependencyValidationSymbols.EXCLUDE + entityName[0])) {
                // If we have supplied an explicitly excluded item then return null
                return Pair.of(level, null);
            } else if (cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, null);
            } else if (cache.containsKey(entityName[0])) {
                // Item explicitly listed - Proceed
                return shim(level, cache, entityName[0], entityName, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return shim(level, cache, DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY, entityName, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return shim(level, cache, DependencyValidationSymbols.INCLUDE_ALL, entityName, keys);
            } else if (cache.containsKey(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We should not be here
                return Pair.of(level, null);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, null);
            }
        } else {
            return Pair.of(level, null);
        }
    }

    private Pair<ControlledListsList,List<DependentEntity>> doTraverse(ControlledListsList level, Set cache, String[] entityName, List<String> keys) {
        if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.contains(DependencyValidationSymbols.EXCLUDE + entityName[0])) {
                // If we have supplied an explicitly excluded item then return null
                return Pair.of(level, null);
            } else if (cache.contains(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, null);
            } else if (cache.contains(entityName[0])) {
                // Item explicitly listed - Proceed
                return Pair.of(level, null);
            } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return Pair.of(level, null);
            } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return Pair.of(level, null);
            } else if (cache.contains(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We should not be here
                return Pair.of(level, null);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, null);
            }
        } else {
            return Pair.of(level, null);
        }
    }

    public Pair<ControlledListsList, List<DependentEntity>> getFilteredList() {
        return null;
    }

    // Wrong - you have to traverse the hierarchy - using something analagous to the functions
    // in the validator. Problem is you just cannot know what you are going to get until you have it.
/*
    public Pair<ControlledListsList, List<DependentEntity>> traverseHierarchy(DependentEntity... entities) {
        // Take the set we are given and find the lowest level in the hierarchy
        // Then get the dao of the level below that loop it validating each entry
        // We need to get the ControlledListList item for each of the entities
        try {
            List<ControlledListsList> lists = Arrays.stream(entities)
                .map(DependentEntity::getControlledListType)
                .sorted(ControlledListsList.hierarchyOrder)
                .collect(Collectors.toList());

            ControlledListsList deepest = lists.stream().max(ControlledListsList.hierarchyOrder).get();
            ControlledListsList listItem = deepest.next();
            Class<? extends EntityDao> listItemDaoClass = listItem.getDao();

            // Get the DAO from spring
            EntityDao<? extends DependentEntity> listItemDao = applicationContext.getBean(listItemDaoClass);
            List<? extends DependentEntity> itemList = listItemDao.list();
            List<DependentEntity> resultList = new ArrayList();

            // Run the validator
            for (DependentEntity e : itemList) {
                Pair<ControlledListsList, DependencyValidation.Result> result = dependencyValidation.validate(e, entities);
                if (isOk(result)) {
                    resultList.add(e);
                }
            }

            return Pair.of(listItem, resultList);
        } catch (Exception e) {
            LOGGER.error("Given types are invalid: " + entities);
            return null;
        }
    }
*/

    /*
     * Returns a the list of entities at at the child level to the one being specified skipping
     * any levels that are not specified
     *
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // Helpers
    boolean isOk(Pair<ControlledListsList, DependencyValidation.Result> result) {
        return result.getRight().equals(DependencyValidation.Result.OK);
    }

    boolean isNotExpected(Pair<ControlledListsList, DependencyValidation.Result> result) {
        return result.getRight().equals(DependencyValidation.Result.NOT_EXPECTED);
    }

}
