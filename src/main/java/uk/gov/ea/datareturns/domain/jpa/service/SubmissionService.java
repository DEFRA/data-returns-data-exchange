package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.SubmissionType;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.Payload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
    private final SubmissionDao submissionDao;

    @Inject
    public SubmissionService(UserDao userDao, DatasetDao datasetDao, RecordDao recordDao,
                             RecordStatusDao recordStatusDao, SubmissionDao submissionDao) {
        this.datasetDao =  datasetDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
        this.recordStatusDao = recordStatusDao;
        this.submissionDao = submissionDao;
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
     * Create a new submission record for a dataset. The and dataset and record is system managed
     * @return
     */
    @Transactional
    public Record createRecord() {
        Record record = generateDetachedRecord(createDataset(), UUID.randomUUID().toString());
        return recordDao.add(record);
    }

    /**
     * Create a new submission record for a dataset. The record is system managed
     * @param dataset
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset) {
        Record record = generateDetachedRecord(dataset, UUID.randomUUID().toString());
        return recordDao.add(record);
    }

    /**
     * Create a new submission record for a dataset. The record is system managed
     * @param dataset
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset, String identifier) {
        Record record = generateDetachedRecord(dataset, identifier);
        return recordDao.add(record);
    }

    /**
     * Create a new submission record for a dataset. The record is user managed
     * @param dataset
     * @return
     */
    public Record generateDetachedRecord(Dataset dataset, String identifier) {
        Date now = new Date();
        Record record = new Record();
        record.setDataset(dataset);
        record.setIdentifier(identifier);
        RecordStatus recordStatus = recordStatusDao.getStatus(RecordStatus.UNINITIALIZED);
        record.setRecordStatus(recordStatus);
        record.setCreateDate(now);
        record.setEtag(UUID.randomUUID().toString());
        record.setLastChangedDate(now);
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
    /**
     * Persist validated samples - will fail if the samples are invalid
     * @param dataset - the dataset to associated with the saved samples
     * @param samples - an array of samples to persist. The records will
     *                be automatically generated
     * @return the number of persisted samples
     */
    @Transactional
    public long submit(Dataset dataset, List<DataSample> samples) {
        long ctr = 0L;
        for (DataSample t : samples) {
            DataSampleSubmission dataSampleSubmission = t.toSubmissionType();
            Record record = generateDetachedRecord(dataset, UUID.randomUUID().toString());
            record.setDataSampleSubmission(dataSampleSubmission);
            dataSampleSubmission.setRecord(record);
            recordDao.add(record);
            if (record.getId() != null) {
                ctr++;
            }
        }
        return ctr;
    }

    @Transactional(readOnly = true)
    public List<Dataset> getDatasets(User user) {
        return datasetDao.list(user);
    }

    public List<Record> getRecords(Dataset dataset) {
        return recordDao.list(dataset);
    }
}
