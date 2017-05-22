package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ObservationDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.Mvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.MvoFactory;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.ObservationValidator;
import uk.gov.ea.datareturns.web.resource.ObservationSerializationBean;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 *         A submission service is responsible for managing the lifecycle of a submission:
 *         <p>
 *         (1) CREATED. A submission record is created in the system which may have an identifier
 *         set by the user or generated automatically by the system. There is no observationSerializationBean (submission data)
 *         associated with a record at this point.
 *         <p>
 *         (2) PARSED. The use supplies a Json string which is parsed as valid json for the
 *         submission type and stored on the record.
 *         <p>
 *         (3) INVALID. The Data in the Json string has been validated and found to be invalid. The
 *         resultant error is stored in on the record
 *         <p>
 *         (4) VALID. The data in the Json string has been validated and found to be valid. Any errors are
 *         cleared from the error field
 *         <p>
 *         (5) SUBMITTED. The validated data is used to persist the submission and all of its relationships.
 *         <p>
 *         The service is created by the submission service configuration
 */
public class SubmissionService<D extends ObservationSerializationBean, M extends AbstractObservation, V extends Mvo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);

    private final Class<D> observationSerializationBeanClass;
    private final Class<D[]> observationSerializationBeanArrayClass;
    private final MvoFactory<D, V> mvoFactory;
    private final UserDao userDao;
    private final DatasetDao datasetDao;
    private final RecordDao recordDao;
    private final ObservationDao<M> measurementDao;
    private final ObservationValidator<V> validator;
    private final AbstractObservationFactory<M, D> abstractObservationFactory;

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * @param observationSerializationBeanClass Class of the data transfer object
     * @param userDao             The user data access object
     * @param datasetDao          The dataset data access object
     * @param recordDao           The record data access object
     * @param validator           The validator to be used
     */
    public SubmissionService(Class<D> observationSerializationBeanClass,
            Class<D[]> observationSerializationBeanArrayClass,
            MvoFactory<D, V> mvoFactory,
            UserDao userDao,
            DatasetDao datasetDao,
            RecordDao recordDao,
            ObservationDao<M> submissionDao,
            ObservationValidator<V> validator,
            AbstractObservationFactory<M, D> abstractObservationFactory) {

        this.observationSerializationBeanClass = observationSerializationBeanClass;
        this.observationSerializationBeanArrayClass = observationSerializationBeanArrayClass;
        this.mvoFactory = mvoFactory;
        this.userDao = userDao;
        this.datasetDao = datasetDao;
        this.recordDao = recordDao;
        this.measurementDao = submissionDao;
        this.validator = validator;
        this.abstractObservationFactory = abstractObservationFactory;

        LOGGER.info("Initializing submission service for datum type: " + observationSerializationBeanClass.getSimpleName());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    }

    /****************************************************************************************
     * RecordEntity and submission centric operations
     *
     * Bulk record and submission centric operations require a the use of nested class to
     * couple given identifiers and observationSerializationBeans either of which may be given as null
     ****************************************************************************************/
    public static class ObservationIdentifierPair<D> {
        final String identifier;
        final D datum;

        public ObservationIdentifierPair(String identifier, D datum) {
            this.identifier = identifier;
            this.datum = datum;
        }

        public ObservationIdentifierPair(D datum) {
            this.identifier = UUID.randomUUID().toString();
            this.datum = datum;
        }

        public ObservationIdentifierPair(String identifier) {
            this.identifier = identifier;
            this.datum = null;
        }

        public ObservationIdentifierPair() {
            this.identifier = UUID.randomUUID().toString();
            this.datum = null;
        }

    }

    /**
     * Parse a JSON string and create the data transfer objects. Used primarily for testing
     * as jersey will be responsible for the initial deserialization of messages
     *
     * @param json The json string to parseJsonArray
     * @return A list of data transfer objects
     */
    public List<D> parseJsonArray(String json) {
        try {
            return Arrays.asList(mapper.readValue(json, observationSerializationBeanArrayClass));
        } catch (IOException e) {
            LOGGER.info("Cannot parseJsonArray JSON: " + json + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse a JSON string and create the data transfer objects. Used primarily for testing
     * as jersey will be responsible for the initial deserialization of messages
     *
     * @param json The json string to parseJsonArray
     * @return A list of data transfer objects
     */
    public D parseJsonObject(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return (mapper.readValue(json, observationSerializationBeanClass));
        } catch (IOException e) {
            LOGGER.info("Cannot parseJsonArray JSON: " + json + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a list of existing records for a given dataset
     *
     * @param dataset A dataset
     * @return A list of records
     */
    @Transactional(readOnly = true)
    public List<RecordEntity> getRecords(DatasetEntity dataset) {
        return recordDao.list(dataset);
    }

    /**
     * Retrieve a single record null if not found
     * @param dataset A dataset
     * @param identifier A record identifier
     * @return the record
     */
    @Transactional(readOnly = true)
    public RecordEntity getRecord(DatasetEntity dataset, String identifier) {
        return recordDao.get(dataset, identifier);
    }

    /**
     * Remove a record using its identifier
     *
     * @param dataset    The dataset
     * @param identifier the record identifier
     */
    @Transactional
    public void removeRecord(DatasetEntity dataset, String identifier) {
        recordDao.remove(dataset, identifier);
    }

    /**
     * Remove a given record
     * @param recordEntity
     */
    @Transactional
    public void removeRecord(RecordEntity recordEntity) {
        recordDao.remove(recordEntity);
    }

    /**
     * Test if a record exists in a given dataset by its identifier
     *
     * @param dataset    The dataset
     * @param identifier The identifier
     * @return
     */
    public boolean recordExists(DatasetEntity dataset, String identifier) {
        return recordDao.get(dataset, identifier) != null;
    }

    /**
     * Creates new records associated with a given dataset.
     * The record status will be set to CREATED if no datum
     * is specified or PARSED if a datum is specified
     *
     * @param dataset
     * @param datumIdentifierPairs
     * @return A list of records
     */
    @Transactional
    public List<RecordEntity> createRecords(DatasetEntity dataset, List<ObservationIdentifierPair<D>> datumIdentifierPairs) {
        return datumIdentifierPairs.stream()
                .map(p -> createOrResetRecord(dataset, p.identifier, p.datum))
                .collect(Collectors.toList());
    }

    /**
     * Creates new record associated with a given dataset.
     * The record status will be set to CREATED if no datum
     * is specified or PARSED if a datum is specified
     *
     * @param dataset
     * @param datumIdentifierPair
     * @return A list of records
     */
    @Transactional
    public RecordEntity createRecord(DatasetEntity dataset, ObservationIdentifierPair<D> datumIdentifierPair) {
        return createOrResetRecord(dataset, datumIdentifierPair.identifier, datumIdentifierPair.datum);
    }

    /**
     * Validate a recordEntity which has been created with a sample (observationSerializationBean)
     * The validation is against the associated validation object (MVO)
     *
     * @param record The record entity to validate
     */
    @Transactional
    public void validate(RecordEntity record) {
        D observationSerializationBean = null;
        V validationObject;

        if (record.getJson() != null && !record.getJson().isEmpty()) {
            try {
                observationSerializationBean = mapper.readValue(record.getJson(), observationSerializationBeanClass);
            } catch (IOException e) {
                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
            }
            validationObject = mvoFactory.create(observationSerializationBean);
            Set<ValidationError> validationErrors = validator.validateObservation(validationObject);

            if (validationErrors.size() == 0) {
                record.setRecordStatus(RecordEntity.RecordStatus.VALID);
            } else {
                //String result = mapper.writeValueAsString(validationResult);
                record.setValidationErrors(validationErrors);
                record.setRecordStatus(RecordEntity.RecordStatus.INVALID);
            }
            record.setLastChangedDate(Instant.now());
            recordDao.merge(record);
        }
    }

    /**
     * Validate a set of recordEntities which have been created with samples
     * The validation is against the associated validation object (MVO)
     * The JSON stored in the record by the
     *
     * @param recordEntities The recordEntities to validate
     */
    @Transactional
    public void validate(Collection<RecordEntity> recordEntities) {
        // Deserialize the list of samples from the JSON
        // field in the record and pass store in a map
        Map<RecordEntity, V> mvos = recordEntities.stream()
                .filter(r -> r.getJson() != null && !r.getJson().isEmpty())
                .collect(Collectors.toMap(
                        r -> r,
                        r -> {
                            D observationSerializationBean;
                            try {
                                observationSerializationBean = mapper.readValue(r.getJson(), observationSerializationBeanClass);
                            } catch (IOException e) {
                                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                                return null;
                            }
                            return mvoFactory.create(observationSerializationBean);
                        }
                ));

        // Validate the Mvo measurement record and store the result of the validation
        // as the validation result serialized to json
        mvos.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(m -> {
                    Set<ValidationError> validationErrors = validator.validateObservation(m.getValue());
                    if (validationErrors.size() == 0) {
                        m.getKey().setRecordStatus(RecordEntity.RecordStatus.VALID);
                    } else {
                        m.getKey().setValidationErrors(validationErrors);
                        m.getKey().setRecordStatus(RecordEntity.RecordStatus.INVALID);
                    }
                    m.getKey().setLastChangedDate(Instant.now());
                    recordDao.merge(m.getKey());
                });
    }

    /**
     * Submit a set of valid recordEntities - this writes the data from the stored JSON into the
     * relation database structures - it creates an instance of a class inheriting AbstractObservation
     * and associates it with the record.
     * <p>
     * It will ignore all recordEntities that are invalid
     *
     * @param recordEntities
     */
    @Transactional
    public void submit(List<RecordEntity> recordEntities) {
        recordEntities.stream()
                .filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID)
                .forEach(r -> {
                    try {
                        M submission = abstractObservationFactory.create(mapper.readValue(r.getJson(), observationSerializationBeanClass));
                        r.setAbstractObservation(submission);
                        r.setRecordStatus(RecordEntity.RecordStatus.SUBMITTED);
                        r.getAbstractObservation().setRecordEntity(r);
                        r.setLastChangedDate(Instant.now());
                        measurementDao.persist(submission);
                        recordDao.merge(r);
                    } catch (IOException e) {
                        LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                    }
                });
    }

    /**
     * Submit a set of valid recordEntities - this writes the data from the stored JSON into the
     * relation database structures - it creates an instance of a class inheriting AbstractObservation
     * and associates it with the record.
     * <p>
     * It will ignore all recordEntities that are invalid
     *
     * @param recordEntities
     */
    @Transactional
    public void evaluateSubstitutes(List<RecordEntity> recordEntities) {
        recordEntities.stream()
            .filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID)
            .forEach(r -> {
                try {
                    M submission = abstractObservationFactory.create(mapper.readValue(r.getJson(), observationSerializationBeanClass));
                    LOGGER.info(submission.toString());
                } catch (IOException e) {
                    LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                }
            });
    }

    /**
     * Creates a new record on a given identifier OR resets a record
     * on a given identifier if it already exists.
     * <p>
     * If a valid datum is supplied the record status is set to PARSED
     * otherwise it is set to created.
     * <p>
     * Any errors are cleared and the record values
     * are reset
     * <p>
     * Any record with a status if SUBMITTED remains unaffected by this call
     *
     * @return
     */
    private RecordEntity createOrResetRecord(DatasetEntity dataset, String identifier, D observationSerializationBean) {
        RecordEntity recordEntity = getOrCreateRecord(dataset, identifier);
        RecordEntity.RecordStatus newRecordStatus = recordEntity.getRecordStatus();

        if (newRecordStatus == RecordEntity.RecordStatus.SUBMITTED) {
            return recordEntity;
        }

        // If we have a data transfer object, associated
        if (observationSerializationBean != null) {
            try {
                String json = mapper.writeValueAsString(observationSerializationBean);
                recordEntity.setJson(json);
                recordEntity.setRecordStatus(RecordEntity.RecordStatus.PARSED);
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot serialize to Json: " + observationSerializationBean.toString());
            }
        } else {
            recordEntity.setRecordStatus(RecordEntity.RecordStatus.PERSISTED);
            recordEntity.setJson(null);
        }

        // If its a new recordEntity persist it or otherwise merge
        if (newRecordStatus == RecordEntity.RecordStatus.CREATED) {
            return recordDao.persist(recordEntity);
        } else {
            recordEntity.setLastChangedDate(Instant.now());
            recordDao.merge(recordEntity);
            return recordEntity;
        }
    }

    /**
     * Returns a submitted measurements for a dataset and identifier
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public RecordEntity retrieve(DatasetEntity dataset, String id) {
        return recordDao.getMeasurement(dataset, id);
    }

    /**
     * Returns the set of submitted measurements in a dataset
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public List<RecordEntity> retrieve(DatasetEntity dataset) {
        return recordDao.listMeasurements(dataset);
    }

    /**
     * Returns a new and initialized record for a given dataset
     * with a system generated identifier
     * It does not persist the record
     *
     * @param dataset
     * @return The initialized record
     */
    private RecordEntity getOrCreateRecord(DatasetEntity dataset) {
        return getOrCreateRecord(dataset, UUID.randomUUID().toString());
    }

    /**
     * Returns either a new and initialized record for a dataset and identifier
     * or if the identified record already exists it returns the existing record
     * It does not persist the record.
     *
     * @param dataset
     * @param identifier
     * @return
     */
    private RecordEntity getOrCreateRecord(DatasetEntity dataset, String identifier) {
        RecordEntity recordEntity = recordDao.get(dataset, identifier);
        if (recordEntity == null) {
            recordEntity = new RecordEntity();
            recordEntity.setDataset(dataset);
            recordEntity.setIdentifier(identifier);
            recordEntity.setRecordStatus(RecordEntity.RecordStatus.CREATED);

            Instant timestamp = Instant.now();
            recordEntity.setCreateDate(timestamp);
            recordEntity.setLastChangedDate(timestamp);
        }
        return recordEntity;
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
        if (newDatasetEntity.getIdentifier() == null) {
            newDatasetEntity.setIdentifier(UUID.randomUUID().toString());
        }
        if (newDatasetEntity.getUser() == null) {
            newDatasetEntity.setUser(getSystemUser());
        }
        datasetDao.persist(newDatasetEntity);
    }

    @Transactional
    public void updateDataset(DatasetEntity datasetEntity) {
        datasetEntity.setLastChangedDate(Instant.now());
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
        datasetDao.remove(getSystemUser(), identifier);
    }

    @Transactional
    public void removeDataset(String identifier, User user) {
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
