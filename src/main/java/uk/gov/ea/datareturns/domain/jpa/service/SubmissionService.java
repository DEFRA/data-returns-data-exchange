package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Submission;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.Datum;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 * The submission service is responsible for managing the lifecycle of a submission:
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
 */
public class SubmissionService<T extends Datum> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);
    private final DatasetDao datasetDao;
    private final UserDao userDao;
    private final RecordDao recordDao;
    private final SubmissionDao submissionDao;
    private final Class<T[]> dataSampleClass;
    private final DataSampleValidator validator;
    private final static ObjectMapper mapper = new ObjectMapper();

    @Inject
    public SubmissionService(Class<T[]> dataSampleClass, DataSampleValidator validator,
                             UserDao userDao, DatasetDao datasetDao, RecordDao recordDao,
                             SubmissionDao submissionDao
                             ) {

        this.dataSampleClass = dataSampleClass;
        this.validator = validator;
        this.datasetDao =  datasetDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
        this.submissionDao = submissionDao;

        LOGGER.info("Initializing submission service for datum type: " + dataSampleClass.getSimpleName());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.setAnnotationIntrospector(new IgnoreInheritedIntrospector());
    }

    private static class IgnoreInheritedIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public boolean hasIgnoreMarker(final AnnotatedMember m) {
            return false ;//m.getDeclaringClass() == Base.class || super.hasIgnoreMarker(m);
        }
    }

    /****************************************************************************************
     * Json serialization and deserialization operations
     ****************************************************************************************/

    /**
     * Parse JSON and return the an array of samples OR null if the JSON cannot be parsed
     */
    public List<T> parse(String json) {
        try {
            return Arrays.asList(mapper.readValue(json, dataSampleClass));
        } catch (IOException e) {
            LOGGER.info("Cannot parse JSON: " + json);
            return null;
        }
    }

    /****************************************************************************************
     * User centric
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

    @Transactional(readOnly = true)
    public List<Dataset> getDatasets(User user) {
        return datasetDao.list(user);
    }

    @Transactional
    public void removeDataset(String identifier) {
        datasetDao.remove(identifier);
    }

    /****************************************************************************************
     * Record and submission centric operations
     *
     * Bulk record and submission centric operations require a the use of nested class to
     * couple given identifiers and payloads either of which may be given as null
     ****************************************************************************************/
    public static class DatumIdentifierPair<T> {
        protected final String identifier;
        protected final T datum;

        public DatumIdentifierPair(String identifier, T datum) {
            this.identifier = identifier;
            this.datum = datum;
        }

        public DatumIdentifierPair(T datum) {
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
    public List<Record> createRecords(Dataset dataset, List<DatumIdentifierPair<T>> datumIdentifierPairs) {
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
    private Record createOrResetRecord(Dataset dataset, String identifier, T datum) {
        Record record = getRecord(dataset, identifier);
        Record.RecordStatus newRecordStatus = record.getRecordStatus();
        Date now = new Date();

        // If we have a datum associated it
        if (datum != null) {
            String json = null;
            try {
                json = mapper.writeValueAsString(datum);
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

    @Transactional
    public ValidationErrors validate(List<Record> records) {
        // Get a map of the samples
        Map<Record, DataSample> samples = records.stream()
                //.filter(r -> r.getJson().isEmpty())
                .collect(Collectors.toMap(v -> v, v -> {
                    try {
                        return mapper.readValue(v.getJson(), DataSample.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }));

        return null;
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
        boolean newSubmission = false;
        Submission submission;

        // If the record contains a submission then use it
        if (record.getSubmission() == null) {
            submission = dataSample.toSubmission();
            record.setSubmission(submission);
            submission.setRecord(record);
            newSubmission = true;
        } else {
            submission = record.getSubmission();
            dataSample.toSubmission(submission);
        }

        record.setRecordStatus(Record.RecordStatus.CREATED);
        record.setLastChangedDate(now);
        record.setEtag(UUID.randomUUID().toString());

        // If the record is new persist it otherwise merge the changes
        if (record.getId() == null) {
            recordDao.persist(record);
        } else {
            recordDao.merge(record);
        }

        if (newSubmission) {
            submissionDao.persist(submission);
        } else {
            submissionDao.merge(submission);
        }

    }


}
