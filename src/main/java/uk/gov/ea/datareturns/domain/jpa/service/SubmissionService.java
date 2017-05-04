package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.dto.MeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 * A submission service is responsible for managing the lifecycle of a submission:
 *
 * (1) CREATED. A submission record is created in the system which may have an identifier
 * set by the user or generated automatically by the system. There is no payload (submission data)
 * associated with a record at this point.
 *
 * (2) PARSED. The use supplies a Json string which is parsed as valid json for the
 * submission type and stored on the record.
 *
 * (3) INVALID. The Data in the Json string has been validated and found to be invalid. The
 * resultant error is stored in on the record
 *
 * (4) VALID. The data in the Json string has been validated and found to be valid. Any errors are
 * cleared from the error field
 *
 * (5) SUBMITTED. The validated data is used to persist the submission and all of ite relationships.
 * 
 * The service is created by the submission service configuration
 *
 */
public class SubmissionService<DTO extends MeasurementDto, M extends AbstractMeasurement> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);

    private final Class<DTO> measurementDtoClass;
    private final Class<M> measurementClass;
    private final UserDao userDao;
    private final DatasetDao datasetDao;
    private final RecordDao recordDao;
    private final Class<DTO[]> measurementDtoArrayClass;
    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * @param measurementDtoClass Class of the data transfer object
     * @param measurementClass The class of the measurement entity to persist
     * @param userDao The user data access object
     * @param datasetDao The dataset data access object
     * @param recordDao The record data access object
     */
    public SubmissionService(Class<DTO> measurementDtoClass,
                             Class<DTO[]> measurementDtoArrayClass,
                             Class<M> measurementClass,
                             UserDao userDao,
                             DatasetDao datasetDao, 
                             RecordDao recordDao) {

        this.measurementDtoClass = measurementDtoClass;
        this.measurementDtoArrayClass = measurementDtoArrayClass;
        this.measurementClass = measurementClass;
        this.userDao = userDao;
        this.datasetDao = datasetDao;
        this.recordDao = recordDao;

        LOGGER.info("Initializing submission service for datum type: " + measurementDtoClass.getSimpleName());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    }

    /****************************************************************************************
     * Record and submission centric operations
     *
     * Bulk record and submission centric operations require a the use of nested class to
     * couple given identifiers and payloads either of which may be given as null
     ****************************************************************************************/
    public static class DatumIdentifierPair<DTO> {
        protected final String identifier;
        protected final DTO datum;

        public DatumIdentifierPair(String identifier, DTO datum) {
            this.identifier = identifier;
            this.datum = datum;
        }

        public DatumIdentifierPair(DTO datum) {
            this.identifier = UUID.randomUUID().toString();
            this.datum = datum;
        }

        public DatumIdentifierPair(String identifier) {
            this.identifier = identifier;
            this.datum = null;
        }

        public DatumIdentifierPair() {
            this.identifier = UUID.randomUUID().toString();
            this.datum = null;
        }
    }

    /**
     * Parse a JSON string and create the DTO objects.
     * @param json
     * @return
     */
    public List<DTO> parse(String json) {
        try {
            return Arrays.asList(mapper.readValue(json, measurementDtoArrayClass));
        } catch (IOException e) {
            LOGGER.info("Cannot parse JSON: " + json);
            return null;
        }
    }

    /**
     * Get a list of existing records for a given dataset
     * @param dataset
     * @return
     */
    @Transactional(readOnly = true)
    public List<Record> getRecords(Dataset dataset) {
        return recordDao.list(dataset);
    }

    /**
     * Remove a record using its identifier
     * @param dataset The dataset
     * @param identifier the record identifier
     */
    @Transactional
    public void removeRecord(Dataset dataset, String identifier) {
        recordDao.remove(dataset, identifier);
    }

    /**
     * Test if a record exists in a given dataset by its identifier
     * @param dataset The dataset
     * @param identifier The identifier
     * @return
     */
    public boolean recordExists(Dataset dataset, String identifier) {
        return (recordDao.get(dataset, identifier) == null) ? false : true;
    }

    /**
     * Creates new records associated with a given dataset.
     * The record status will be set to CREATED if no datum
     * is specified or PARSED if a datum is specified
     * @param dataset
     * @param datumIdentifierPairs
     * @return A list of records
     */
    @Transactional
    public List<Record> createRecords(Dataset dataset, List<SubmissionService.DatumIdentifierPair<DTO>> datumIdentifierPairs) {
        return datumIdentifierPairs.stream()
                .map(p -> createOrResetRecord(dataset, p.identifier, p.datum))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new record on a given identifier OR resets a record
     * on a given identifier if it already exists.
     *
     * If a valid datum is supplied the record status is set to PARSED
     * otherwise it is set to created.
     *
     * Any errors are cleared and the record values
     * are reset
     *
     * @return
     */
    private Record createOrResetRecord(Dataset dataset, String identifier, DTO dto) {
        Record record = getRecord(dataset, identifier);
        Record.RecordStatus newRecordStatus = record.getRecordStatus();
        Date now = new Date();

        // If we have a datum associated it
        if (dto != null) {
            String json = null;
            try {
                json = mapper.writeValueAsString(dto);
                record.setJson(json);
                record.setRecordStatus(Record.RecordStatus.PARSED);
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot serialize Json: " + json);
            }
        } else {
            record.setRecordStatus(Record.RecordStatus.PERSISTED);
            record.setJson(null);
        }

        // If its a new record persist it or otherwise merge
        if (newRecordStatus == Record.RecordStatus.CREATED) {
            return recordDao.persist(record);
        } else {
            record.setLastChangedDate(now);
            recordDao.merge(record);
            return record;
        }
    }

    /**
     * Returns a new and initialized record for a given dataset
     * with a system generated identifier
     * It does not persist the record
     * @param dataset
     * @return The initialized record
     */
    private Record getRecord(Dataset dataset) {
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
    private Record getRecord(Dataset dataset, String identifier) {
        Record record = recordDao.get(dataset, identifier);
        if (record == null) {
            Date now = new Date();
            record = new Record();
            record.setDataset(dataset);
            record.setIdentifier(identifier);
            record.setRecordStatus(Record.RecordStatus.CREATED);
            record.setCreateDate(now);
            record.setLastChangedDate(now);
            record.setEtag(UUID.randomUUID().toString());
        }
        return record;
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

    @Transactional(readOnly = true)
    public List<Dataset> getDatasets(User user) {
        return datasetDao.list(user);
    }

    @Transactional
    public void removeDataset(String identifier) {
        datasetDao.remove(identifier);
    }

}
