package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Graham Willis
 */
public class DatasetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);
    private final UserDao userDao;
    private final DatasetDao datasetDao;

    public DatasetService(UserDao userDao, DatasetDao datasetDao) {
        this.userDao = userDao;
        this.datasetDao = datasetDao;
        LOGGER.info("Initializing dataset service");
    }
    /**
     * Get the default (system) user
     *
     * @return
     */
    @Transactional(readOnly = true)
    public User getSystemUser() {
        return userDao.getSystemUser();
    }

    /**
     * Create a new user
     *
     * @param identifier The username
     * @return The user
     */
    @Transactional
    public User createUser(String identifier) {
        User user = new User();
        user.setIdentifier(identifier);
        user.setCreateDate(Instant.now());
        user.setLastChangedDate(Instant.now());
        userDao.persist(user);
        return user;
    }

    /**
     * Get a user entity from its identifier
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public User getUser(String identifier) {
        return userDao.get(identifier);
    }

    /**
     * Remove a user entity by its identifier
     *
     * @param identifier
     */
    @Transactional
    public void removeUser(String identifier) {
        userDao.remove(identifier);
    }

    /****************************************************************************************
     * DatasetEntity centric operations
     ****************************************************************************************/

    /**
     * Create a persisted dataset from a detached entity
     *
     * @param newDatasetEntity
     */
    @Transactional
    public void createDataset(DatasetEntity newDatasetEntity) {
        Instant timestamp = Instant.now();
        newDatasetEntity.setCreateDate(timestamp);
        newDatasetEntity.setLastChangedDate(timestamp);
        newDatasetEntity.setStatus(DatasetEntity.Status.UNSUBMITTED);

        if (newDatasetEntity.getIdentifier() == null) {
            newDatasetEntity.setIdentifier(UUID.randomUUID().toString());
        }
        if (newDatasetEntity.getUser() == null) {
            newDatasetEntity.setUser(getSystemUser());
        }

        User user = userDao.get(newDatasetEntity.getUser().getIdentifier());
        user.setDatasetChangedDate(timestamp);

        userDao.merge(user);
        datasetDao.persist(newDatasetEntity);
    }

    @Transactional
    public void updateDataset(DatasetEntity datasetEntity) {
        Instant timestamp = Instant.now();
        datasetEntity.setLastChangedDate(Instant.now());
        User user = userDao.get(datasetEntity.getUser().getIdentifier());
        user.setDatasetChangedDate(timestamp);

        userDao.merge(user);
        datasetDao.merge(datasetEntity);
    }

    @Transactional(readOnly = true)
    public List<DatasetEntity> getDatasets(User user) {
        return datasetDao.list(user);
    }

    @Transactional(readOnly = true)
    public List<DatasetEntity> getDatasets() {
        return datasetDao.list(getSystemUser());
    }

    @Transactional
    public void removeDataset(String identifier) {
        removeDataset(identifier, getSystemUser());
    }

    @Transactional
    public void removeDataset(String identifier, User user) {
        Instant timestamp = Instant.now();
        User usrAtt = userDao.get(user.getIdentifier());
        usrAtt.setDatasetChangedDate(timestamp);
        userDao.merge(usrAtt);
        datasetDao.remove(user, identifier);
    }

    @Transactional(readOnly = true)
    public DatasetEntity getDataset(String datasetId) {
        return getDataset(datasetId, getSystemUser());
    }

    @Transactional(readOnly = true)
    public DatasetEntity getDataset(String datasetId, User user) {
        return datasetDao.get(user, datasetId);
    }
}
