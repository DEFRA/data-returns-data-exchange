package uk.gov.ea.datareturns.domain.processors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.ControlledListsDto;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Graham Willis
 * Service to handle operations on controlled lists
 */
@SuppressWarnings("unchecked")
@Component
public class ControlledListProcessor implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListProcessor.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Return metadata about controlled lists
     * @return
     */
    public Map<String, ControlledListsDto> getListMetaData() {
        Map<String, ControlledListsDto> result = new HashMap<>();
        for (ControlledListsList list : ControlledListsList.values()) {
            result.put(list.getPath(), new ControlledListsDto(list.getDescription(), list.getPath(), list.getDisplayHeaders(),
                    list.getDefaultSearch()));
        }
        return result;
    }

    /**
     * Return controlled list data for a given list.
     * @param controlledList The controlled list for which to return data
     * @return
     */
    private Pair<String, List<? extends ControlledListEntity>> getListData(ControlledListsList controlledList) {
        EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
        return new ImmutablePair<>(controlledList.getPath(), dao.list());
    }

    /**
     * Return controlled list data for a given list filtered by a single search term on the name
     * @param controlledList
     * @param searchTerm
     * @return
     */
    public Pair<String, List<? extends ControlledListEntity>> getListData(ControlledListsList controlledList, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
            return new ImmutablePair<>(controlledList.getPath(), dao.list(e -> e.getName().toLowerCase().contains(searchTerm)));
        }
    }

    /**
     * Search by an arbituary field containing string
     * @param controlledList The controlled list to return
     * @param field The field to search
     * @param contains The search term
     * @return The filtered list
     */
    public Pair<String, List<? extends ControlledListEntity>> getListData(ControlledListsList controlledList, String field, String contains) {
        if (field == null || field.isEmpty() || contains == null || contains.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            EntityDao<? extends ControlledListEntity> dao = applicationContext.getBean(controlledList.getDao());
            return new ImmutablePair<>(controlledList.getPath(), dao.list(field, contains));
        }
    }

    /**
     * Returns the data at the level in a hierarchy
     * @param hierarchy The hierarchy to interrogate
     * @param entities The list of entities implying the route to the require level
     * @param field
     *@param contains @return A pair of the controlled list path and the list
     */
    public Pair<String, List<? extends Hierarchy.HierarchyEntity>> getListData(Hierarchy hierarchy, Set<Hierarchy.HierarchyEntity> entities, String field, String contains) {
        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> results = null;
        if (field == null || field.isEmpty() || contains == null || contains.isEmpty()) {
            results = hierarchy.children(entities);
        } else {
            results = hierarchy.children(entities, field, contains);
        }
        return new ImmutablePair<>(results.getLeft().getControlledList().getPath(), results.getRight());
    }

    /**
     * return the validation for a group of entities against a hierarchy
     * @param hierarchy The hierarchy to interrogate
     * @param entities The list of entities implying the route to the require level
     */
    public Pair<String, Hierarchy.Result> validate(Hierarchy hierarchy, Set<Hierarchy.HierarchyEntity> entities) {
        Pair<HierarchyLevel, Hierarchy.Result> result = hierarchy.validate(entities);
        return new ImmutablePair<>(result.getLeft().getControlledList().getPath(), result.getRight());
    }

}
