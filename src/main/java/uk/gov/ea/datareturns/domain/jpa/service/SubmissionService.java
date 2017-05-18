package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.web.resource.ObservationSerializationBean;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ObservationDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.ObservationValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.Mvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.MvoFactory;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.result.ValidationResult;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 *         A submission service is responsible for managing the lifecycle of a submission:
 *         <p>
 *         (1) CREATED. A submission record is created in the system which may have an identifier
 *         set by the user or generated automatically by the system. There is no payload (submission data)
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
     * Record and submission centric operations
     *
     * Bulk record and submission centric operations require a the use of nested class to
     * couple given identifiers and payloads either of which may be given as null
     ****************************************************************************************/
    public static class DtoIdentifierPair<D> {
        final String identifier;
        final D datum;

        public DtoIdentifierPair(String identifier, D datum) {
            this.identifier = identifier;
            this.datum = datum;
        }

        public DtoIdentifierPair(D datum) {
            this.identifier = UUID.randomUUID().toString();
            this.datum = datum;
        }

        public DtoIdentifierPair(String identifier) {
            this.identifier = identifier;
            this.datum = null;
        }

        public DtoIdentifierPair() {
            this.identifier = UUID.randomUUID().toString();
            this.datum = null;
        }
    }

    /**
     * Parse a JSON string and create the data transfer objects. Used primarily for testing
     * as jersey will be responsible for the initial deserialization of messages
     *
     * @param json The json string to parse
     * @return A list of data transfer objects
     */
    public List<D> parse(String json) {
        try {
            return Arrays.asList(mapper.readValue(json, observationSerializationBeanArrayClass));
        } catch (IOException e) {
            LOGGER.info("Cannot parse JSON: " + json + ": " + e.getMessage());
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
    public List<Record> getRecords(DatasetEntity dataset) {
        return recordDao.list(dataset);
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
    public List<Record> createRecords(DatasetEntity dataset, List<DtoIdentifierPair<D>> datumIdentifierPairs) {
        return datumIdentifierPairs.stream()
                .map(p -> createOrResetRecord(dataset, p.identifier, p.datum))
                .collect(Collectors.toList());
    }

    /**
     * Validate a set of records which have been created with samples
     * The validation is against the associated validation object (MVO)
     * The JSON stored in the record by the
     *
     * @param records The records to validate
     */
    @Transactional
    public void validate(List<Record> records) {
        // Deserialize the list of samples from the JSON
        // field in the record and pass store in a map
        Map<Record, V> mvos = records.stream()
                .filter(r -> !r.getJson().isEmpty())
                .collect(Collectors.toMap(
                        r -> r,
                        r -> {
                            D dto;
                            try {
                                dto = mapper.readValue(r.getJson(), observationSerializationBeanClass);
                            } catch (IOException e) {
                                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                                return null;
                            }
                            return mvoFactory.create(dto);
                        }
                ));

        // Validate the Mvo measurement record and store the result of the validation
        // as the validation result serialized to json
        mvos.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(m -> {
                    ValidationResult validationResult = validator.validateMeasurement(m.getValue());
                    try {
                        if (validationResult.isValid()) {
                            m.getKey().setRecordStatus(Record.RecordStatus.VALID);
                        } else {
                            String result = mapper.writeValueAsString(validationResult);
                            m.getKey().setValidationResult(result);
                            m.getKey().setRecordStatus(Record.RecordStatus.INVALID);
                        }
                        m.getKey().setLastChangedDate(Instant.now());
                        recordDao.merge(m.getKey());
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                    }
                });
    }

    /**
     * Submit a set of valid records - this writes the data from the stored JSON into the
     * relation database structures - it creates an instance of a class inheriting AbstractObservation
     * and associates it with the record.
     * <p>
     * It will ignore all records that are invalid
     *
     * @param records
     */
    @Transactional
    public void submit(List<Record> records) {
        records.stream()
                .filter(r -> r.getRecordStatus() == Record.RecordStatus.VALID)
                .forEach(r -> {
                    try {
                        M submission = abstractObservationFactory.create(mapper.readValue(r.getJson(), observationSerializationBeanClass));
                        r.setMeasurement(submission);
                        r.setRecordStatus(Record.RecordStatus.SUBMITTED);
                        r.getMeasurement().setRecord(r);
                        r.setLastChangedDate(Instant.now());
                        measurementDao.persist(submission);
                        recordDao.merge(r);
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
    private Record createOrResetRecord(DatasetEntity dataset, String identifier, D dto) {
        Record record = getRecord(dataset, identifier);
        Record.RecordStatus newRecordStatus = record.getRecordStatus();

        if (newRecordStatus == Record.RecordStatus.SUBMITTED) {
            return record;
        }

        // If we have a data transfer object, associated
        if (dto != null) {
            try {
                String json = mapper.writeValueAsString(dto);
                record.setJson(json);
                record.setRecordStatus(Record.RecordStatus.PARSED);
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot serialize to Json: " + dto.toString());
            }
        } else {
            record.setRecordStatus(Record.RecordStatus.PERSISTED);
            record.setJson(null);
        }

        // If its a new record persist it or otherwise merge
        if (newRecordStatus == Record.RecordStatus.CREATED) {
            return recordDao.persist(record);
        } else {
            record.setLastChangedDate(Instant.now());
            recordDao.merge(record);
            return record;
        }
    }

    /**
     * Returns a submitted measurements for a dataset and identifier
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public Record retrieve(DatasetEntity dataset, String id) {
        return recordDao.getMeasurement(dataset, id);
    }

    /**
     * Returns the set of submitted measurements in a dataset
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public List<Record> retrieve(DatasetEntity dataset) {
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
    private Record getRecord(DatasetEntity dataset) {
        return getRecord(dataset, UUID.randomUUID().toString());
    }

    /**
     * Returns either a new and initialized record for a dataset and identifier
     * or if the identified record already exists it returns the existing record
     * It does not persist the record
     *
     * @param dataset
     * @param identifier
     * @return
     */
    public Record getRecord(DatasetEntity dataset, String identifier) {
        Record record = recordDao.get(dataset, identifier);
        if (record == null) {
            record = new Record();
            record.setDataset(dataset);
            record.setIdentifier(identifier);
            record.setRecordStatus(Record.RecordStatus.CREATED);
            record.setCreateDate(Instant.now());
            record.setLastChangedDate(Instant.now());
        }
        return record;
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
        newDatasetEntity.setCreateDate(Instant.now());
        newDatasetEntity.setLastChangedDate(Instant.now());
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
