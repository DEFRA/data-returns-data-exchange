package uk.gov.ea.datareturns.domain.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.ControlledListsDto;
import uk.gov.ea.datareturns.domain.dto.NavigationDto;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyNavigation;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by graham on 26/07/16.
 *
 * Service to handle operations on controlled lists
 */
@SuppressWarnings("unchecked")
@Component
public class ControlledListProcessor implements ApplicationContextAware {

    private ParameterDao parameterDao;
    private ReturnTypeDao returnTypeDao;
    private ReleasesAndTransfersDao releasesAndTransfersDao;
    private DependencyNavigation dependencyNavigation;

    @Inject
    public ControlledListProcessor(ParameterDao parameterDao, ReturnTypeDao returnTypeDao,
                                   ReleasesAndTransfersDao releasesAndTransfersDao,
                                   DependencyNavigation dependencyNavigation) {

        this.parameterDao = parameterDao;
        this.returnTypeDao = returnTypeDao;
        this.releasesAndTransfersDao = releasesAndTransfersDao;
        this.dependencyNavigation = dependencyNavigation;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListProcessor.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<? extends ControlledListEntity> getListData(ControlledListsList controlledList) {
        LOGGER.debug("Get list data: " + controlledList.name());
        EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
        return dao.list();
    }

    public List<? extends ControlledListEntity> getListData(ControlledListsList controlledList, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
            return dao.list(e -> e.getName().toLowerCase().contains(searchTerm));
        }
    }

    public List<? extends ControlledListEntity> getListData(ControlledListsList controlledList, String field, String contains) {
        if (field == null || field.isEmpty() || contains == null || contains.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
            return dao.list(field, contains);
        }
    }

    /**
     * return metadata about controlled lists
     * @return
     */
    public Map<String, ControlledListsDto> getListData() {
        Map<String, ControlledListsDto> result = new HashMap<>();
        //TODO Placeholder data for now
        LocalDate lastUpdate = LocalDate.of(2016, 1, 13);
        for (ControlledListsList list : ControlledListsList.values()) {
            result.put(list.getPath(), new ControlledListsDto(list.getDescription(), list.getPath(), list.getDisplayHeaders(), lastUpdate,
                    list.getDefaultSearch()));
        }
        return result;
    }

    /**
     * Navigate throw dependent controlled lists
     * @param returnTypeName
     * @param releaseTypeName
     * @param parameterName
     * @param contains
     * @return the data transfer object for the result of the navigation
     */
    public NavigationDto getNavigatedListData(String returnTypeName, String releaseTypeName, String parameterName, String contains) {
        ReturnType returnType = returnTypeName == null ? null : returnTypeDao.getByName(returnTypeName);
        ReleasesAndTransfers releasesAndTransfers = releaseTypeName == null ? null : releasesAndTransfersDao.getByName(releaseTypeName);
        Parameter parameter = parameterName == null ? null : parameterDao.getByName(parameterName);
        Pair<ControlledListsList, List<? extends Hierarchy.HierarchyEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers, parameter);
        ControlledListsList controlledList = result.getLeft();
        List returnedList = result.getRight();

        if (contains != null) {
            EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
            List<? extends ControlledListEntity> filteredList = dao.filterByName(returnedList, contains);
            return new NavigationDto(controlledList.getPath(), controlledList.getDescription(), filteredList);
        } else {
            return new NavigationDto(controlledList.getPath(), controlledList.getDescription(), result.getRight());
        }
    }
}
