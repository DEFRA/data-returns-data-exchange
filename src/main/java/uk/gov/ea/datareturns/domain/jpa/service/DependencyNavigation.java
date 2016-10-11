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
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Pair<ControlledListsList, List<DependentEntity>> getChildren(ReturnType returnType,
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

        return null;
    }

    // Wrong - you have to traverse the hierarchy - using something analagous to the functions
    // in the validator. Problem is you just cannot know what you are going to get until you have it.
/*
    public Pair<ControlledListsList, List<DependentEntity>> getChildren(DependentEntity... entities) {
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
