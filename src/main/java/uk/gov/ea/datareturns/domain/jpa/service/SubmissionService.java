package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordStatusDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordStatus;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;

import javax.inject.Inject;
import java.util.Date;
import java.util.UUID;

/**
 * @author Graham Willis
 */
@Service
public class SubmissionService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);
    private final DatasetDao datasetDao;
    private final UserDao userDao;
    private final RecordDao recordDao;
    private final RecordStatusDao recordStatusDao;

    @Inject
    public SubmissionService(UserDao userDao, DatasetDao datasetDao, RecordDao recordDao, RecordStatusDao recordStatusDao) {
        this.datasetDao =  datasetDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
        this.recordStatusDao = recordStatusDao;
    }

    /**
     * Create a new dataset where the identifier and user is managed by the system
     * @return the new dataset
     */
    @Transactional
    public Dataset createDataset() {
        return createDataset(getSystemUser(), UUID.randomUUID().toString());
    }

    /**
     * Create a new dataset for a given user where the identifier is managed by the system
     * @param user The owner of the dataset
     * @return The new dataset
     */
    @Transactional
    public Dataset createDataset(User user) {
        return createDataset(user, UUID.randomUUID().toString());
    }

    /**
     * Create a new dataset for a given user where the identifier is given by the user
     * @param user
     * @param identifier
     * @return The new dataset
     */
    @Transactional
    public Dataset createDataset(User user, String identifier) {
        Dataset dataset = new Dataset();
        dataset.setIdentifier(identifier);
        dataset.setUser(user);
        datasetDao.add(dataset);
        return dataset;
    }

    /**
     * Get the default (system) user
     * @return
     */
    @Transactional(readOnly = true)
    public User getSystemUser() {
        return userDao.getSystemUser();
    }

    /**
     * Create a new user
     * @param identifier The username
     * @return The user
     */
    @Transactional
    public User createUser(String identifier) {
        User user = new User();
        user.setIdentifier(identifier);
        userDao.add(user);
        return user;
    }

    /**
     * Get a user entity from its identifier
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public User getUser(String identifier) {
        return userDao.getByIdentifier(identifier);
    }

    /**
     * Remove a user entity by its identifier
     * @param identifier
     */
    @Transactional
    public void removeUser(String identifier) {
        userDao.remove(identifier);
    }

    /**
     * Create a new submission record for a dataset. The record is system managed
     * @param dataset
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset) {
        return createRecord(dataset, UUID.randomUUID().toString());
    }

    /**
     * Create a new submission record for a dataset. The record is user managed
     * @param dataset
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset, String identifier) {
        Date now = new Date();
        Record record = new Record();
        record.setDataset(dataset);
        record.setIdentifier(identifier);
        RecordStatus recordStatus = recordStatusDao.getStatus(RecordStatus.UNVALIDATED);
        record.setRecordStatus(recordStatus);
        record.setCreateDate(now);
        record.setEtag(new byte[] {'1'} );
        record.setLastChangedDate(now);
        recordDao.add(record);
        return record;
    }

    @Transactional(readOnly = true)
    public Dataset getDataset(String identifier) {
        return datasetDao.getByIdentifier(identifier);
    }

    @Transactional
    public void removeDataset(String identifier) {
        datasetDao.remove(identifier);
    }

    @Transactional(readOnly = true)
    public Record getRecord(String identifier) {
        return recordDao.getByIdentifier(identifier);
    }

    @Transactional
    public void removeRecord(String identifier) {
        recordDao.remove(identifier);
    }

    /**
     * Create a new submission record for a dataset using a submission entity. The record is system managed
     * @param dataset
     * @return
     */
    //@Transactional
    //Submission createRecord(Dataset dataset, Submission) {
    //    return null;
    //}

}
