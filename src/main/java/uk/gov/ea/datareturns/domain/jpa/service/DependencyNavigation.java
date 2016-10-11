package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.DependenciesDao;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.DependentEntity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by graham on 11/10/16.
 */
@Component
public class DependencyNavigation implements ApplicationContextAware  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DependencyNavigation.class);
    private ApplicationContext applicationContext;

    @Inject
    private DependencyValidation dependencyValidation;

    @Inject
    private DependenciesDao dao;

    public Pair<ControlledListsList, List<DependentEntity>> getChildren2(DependentEntity... entities) {
        // We need to traverse the cache using the components we are given and substituting
        // wildcards and processing exclusions where necessary

        return null;
    }

    // Wrong - you have to traverse the hierarchy - using something analagous to the functions
    // in the validator. Problem is you just cannot know what you are going to get until you have it.
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
