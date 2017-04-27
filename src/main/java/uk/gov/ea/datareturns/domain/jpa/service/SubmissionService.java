package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Submission;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.model.Datum;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 */
@Service
public class SubmissionService<T extends Datum<? extends Submission>> {
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

    /****************************************************************************************
     * User centric operations
     ****************************************************************************************/

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
        userDao.persist(user);
        return user;
    }

    /**
     * Get a user entity from its identifier
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public User getUser(String identifier) {
        return userDao.get(identifier);
    }

    /**
     * Remove a user entity by its identifier
     * @param identifier
     */
    @Transactional
    public void removeUser(String identifier) {
        userDao.remove(identifier);
    }

    /****************************************************************************************
     * Dataset centric operations
     ****************************************************************************************/

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
        datasetDao.persist(dataset);
        return dataset;
    }

    @Transactional
    public void removeDataset(String identifier) {
        datasetDao.remove(identifier);
    }

    /****************************************************************************************
     * Record and submission centric operations
     ****************************************************************************************/

    /**
     * Create a list of new submission records for a dataset. The records are user managed and identified by
     * identifiers
     * @param dataset
     * @param identifiers a list of identifiers for the records
     * @return
     */
    @Transactional
    public List<Record> createRecords(Dataset dataset, List<String> identifiers) {
        return identifiers.stream()
                .map(p -> getRecord(dataset, p))
                .map(r -> recordDao.persist(r))
                .collect(Collectors.toList());
    }

    /**
     * Create a system managed record and persist
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset) {
        return recordDao.persist(getRecord(dataset));
    }

    /**
     * Create a user managed record and persist
     * @return
     */
    @Transactional
    public Record createRecord(Dataset dataset, String identifier) {
        recordDao.merge(getRecord(dataset, identifier));
        return recordDao.get(dataset, identifier);
    }

    /**
     * Gets a new record for a given dataset and initializes with a system generated identifier
     * It does not persist the record
     * @param dataset
     * @return The initialized record
     */
    public Record getRecord(Dataset dataset) {
        return getRecord(dataset, UUID.randomUUID().toString());
    }

    /**
     * Returns either a new and initialized record for a dataset and identifier
     * or if the identified record already exists it returns the existing record
     * It does not persist the record
     * @param dataset
     * @param identifier
     * @return
     */
    public Record getRecord(Dataset dataset, String identifier) {
        Record record = recordDao.get(dataset, identifier);
        if (record == null) {
            Date now = new Date();
            record = new Record();
            record.setDataset(dataset);
            record.setIdentifier(identifier);
            record.setRecordStatus(recordStatusDao.getStatus(RecordStatus.UNINITIALIZED));
            record.setCreateDate(now);
            record.setLastChangedDate(now);
            record.setEtag(UUID.randomUUID().toString());
        }
        return record;
    }

    /**
     * Persist validated samples - may fail if the samples are invalid
     * @param dataset - the dataset to associated with the saved samples
     * @param samples - an array of samples to persist. The records will
     * be system generated
     *
     * @return the number of persisted samples
     */
    @Transactional
    public void submit(Dataset dataset, List<T> samples) {
        for (T t : samples) {
            Record record = getRecord(dataset);
            submit(record, t);
        }
    }

    /**
     * Persist a supplied given sample against a record
     * @param record
     * @param dataSample
     * @return
     */
    @Transactional
    public void submit(Record record, T dataSample) {
        Date now = new Date();
        Submission submission = dataSample.createSubmissionType();
        record.setSubmission(submission);
        submission.setRecord(record);
        record.setRecordStatus(recordStatusDao.getStatus(RecordStatus.SUBMITTED));
        record.setLastChangedDate(now);
        record.setEtag(UUID.randomUUID().toString());

        // If the record is new persist it otherwise merge the changes and then persis the
        // new submission
        if (record.getId() == null) {
            recordDao.persist(record);
        } else {
            recordDao.merge(record);
        }

        submissionDao.persist(submission);
    }

    @Transactional(readOnly = true)
    public List<Dataset> getDatasets(User user) {
        return datasetDao.list(user);
    }

    @Transactional(readOnly = true)
    public List<Record> getRecords(Dataset dataset) {
        return recordDao.list(dataset);
    }

}
