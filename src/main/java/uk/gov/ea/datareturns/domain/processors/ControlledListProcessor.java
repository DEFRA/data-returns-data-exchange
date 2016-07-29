package uk.gov.ea.datareturns.domain.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.ControlledListsDto;
import uk.gov.ea.datareturns.domain.jpa.dao.AbstractJpaDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.PersistedEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by graham on 26/07/16.
 *
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

    private List<? extends PersistedEntity> getListData(ControlledListsList controlledList) {
        LOGGER.debug("Get list data: " + controlledList.name());
        AbstractJpaDao<? extends PersistedEntity> dao = applicationContext.getBean(controlledList.getDao());
        return dao.list();
    }

    public List<? extends PersistedEntity> getListData(ControlledListsList controlledList, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            AbstractJpaDao<? extends PersistedEntity> dao = applicationContext.getBean(controlledList.getDao());
            return dao.list(e -> e.getName().toLowerCase().contains(searchTerm));
        }
    }

    public List<? extends PersistedEntity> getListData(ControlledListsList controlledList, String field, String contains) {
        if (field == null || field.isEmpty() || contains == null || contains.isEmpty()) {
            return getListData(controlledList);
        } else {
            LOGGER.debug("Get list data filtered by name contains: " + controlledList.name());
            AbstractJpaDao<? extends PersistedEntity>  dao = applicationContext.getBean(controlledList.getDao());
            return dao.list(field, contains);
        }
    }

    /**
     * return metadata about controlled lists
     * @return
     */
    public List<ControlledListsDto> getListData() {
        List<ControlledListsDto> result = new ArrayList<>();
        //TODO Placeholder data for now
        LocalDate lastUpdate = LocalDate.of(2016, 01, 13);
        for(ControlledListsList list : ControlledListsList.values()) {
            result.add(new ControlledListsDto(list.getDescription(), list.getPath(), lastUpdate));
        }
        return result;
    }
}
