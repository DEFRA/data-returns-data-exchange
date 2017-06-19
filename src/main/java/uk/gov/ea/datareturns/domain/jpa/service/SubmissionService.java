package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.lucene.analysis.CharArrayMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadEntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.AbstractValidationObject;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.ValidationObjectFactory;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.Validator;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Graham Willis
 *         A submission service is responsible for managing the lifecycle of a submission:
 *         <p>
 *         (1) CREATED. A submission record is created in the system which may have an identifier
 *         set by the user or generated automatically by the system. There is no payload bean (submission data)
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
public class SubmissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);

    private final ValidationObjectFactory validationObjectFactory;
    private final RecordDao recordDao;
    private final PayloadEntityDao payloadEntityDao;
    private final Validator<AbstractValidationObject> validator;
    private final static ObjectMapper mapper = new ObjectMapper();
    private final DatasetDao datasetDao;

    public SubmissionService(ValidationObjectFactory validationObjectFactory,
            DatasetDao datasetDao,
            RecordDao recordDao,
            PayloadEntityDao payloadEntityDao,
            Validator<AbstractValidationObject> validator) {

        this.validationObjectFactory = validationObjectFactory;
        this.datasetDao = datasetDao;
        this.recordDao = recordDao;
        this.validator = validator;
        this.payloadEntityDao = payloadEntityDao;

        LOGGER.info("Initializing submissions service: ");
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enableDefaultTyping();
    }

    /**
     * Parse a JSON string and create the data transfer objects. Used primarily for testing
     * as jersey will be responsible for the initial deserialization of messages
     *
     * @param json The json string to parseJsonArray
     * @return A list of data transfer objects
     */
    public List<Payload> parseJsonArray(String json) {
        try {
            return (json != null) ? Arrays.asList(mapper.readValue(json, Payload[].class)) : null;
        } catch (IOException e) {
            LOGGER.info("Cannot parse json array: " + json + ": " + e.getMessage());
            return null;
        }
    }

    public Payload parseJsonObject(String json) {
        try {
            return (json != null) ? mapper.readValue(json, Payload.class) : null;
        } catch (IOException e) {
            LOGGER.info("Cannot parse json: " + json + ": " + e.getMessage());
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
        return recordDao.list(dataset, RecordDao.FetchType.FETCH_BASE);
    }

    /**
     * Retrieve records for the given dataset and identifiers
     *
     * @param dataset A dataset
     * @param identifiers a collection of record identifiers to retrieve
     * @return A {@link Map} of records identifiers to their {@link RecordEntity}
     */
    @Transactional(readOnly = true)
    public Map<String, RecordEntity> getRecords(DatasetEntity dataset, Collection<String> identifiers) {
        return recordDao.get(dataset, identifiers);
    }

    /**
     * This returns a list of the invalid records for a dataset
     * with an eager fetch on the validation errors
     * @param dataset
     * @return List of records
     */
    @Transactional(readOnly = true)
    public List<RecordEntity> getInvalidRecords(DatasetEntity dataset) {
        return recordDao.list(dataset, RecordDao.FetchType.FETCH_INVALID);
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
        dataset.setRecordChangedDate(Instant.now());
        datasetDao.merge(dataset);
        recordDao.remove(dataset, identifier);
    }

    /**
     * Remove a given record
     * @param recordEntity
     */
    //@Transactional
    // public void removeRecord(RecordEntity recordEntity) {
    //     dataset.setRecordChangedDate(Instant.now());
    //    datasetDao.merge(dataset);
    //    recordDao.remove(recordEntity);
    //}

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
     * @param dataset the target dataset
     * @param payloadMap a map of identifiers to the payload for that identifier
     * @return a map of updated records
     */
    @Transactional
    public <D extends Payload> Map<String, RecordEntity> createRecords(DatasetEntity dataset, Map<String, D> payloadMap) {
        Map<String, RecordEntity> existingRecords = getRecords(dataset, payloadMap.keySet());

        Map<String, RecordEntity> updatedRecords = new HashMap<>();
        payloadMap.forEach((identifier, payload) -> {
            RecordEntity recordEntity = existingRecords.get(identifier);
            if (recordEntity == null) {
                recordEntity = createRecord(dataset, identifier);
            }
            updatedRecords.put(identifier, updateRecord(recordEntity, payload));
        });
        // Update the dataset's record collection timestamp
        dataset.setRecordChangedDate(Instant.now());
        datasetDao.merge(dataset);

        validate(updatedRecords.values());

        return updatedRecords;
    }

    /**
     * Creates new record associated with a given dataset.
     * The record status will be set to CREATED if no datum
     * is specified or PARSED if a datum is specified
     *
     * @param dataset the dataset to operate on
     * @param identifier the identifier for the new record
     * @param payload the payload to associate with the record
     * @return the resultant {@link RecordEntity}
     */
    @Transactional
    public <D extends Payload> RecordEntity createRecord(DatasetEntity dataset, String identifier, D payload) {
        RecordEntity recordEntity = createOrResetRecord(dataset, identifier, payload);
        validate(recordEntity);
        return recordEntity;
    }

    /**
     *  Validate a recordEntity which has been created with a sample payload
     * @param record
     */
    @Transactional
    public void validate(RecordEntity record) {
        Payload payload = null;
        if (record.getJson() != null && !record.getJson().isEmpty()) {
            try {
                payload = mapper.readValue(record.getJson(), Payload.class);
            } catch (IOException e) {
                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
            }
            AbstractValidationObject validationObject = validationObjectFactory.create(payload);

            Set<ValidationError> validationErrors = validator.validateValidationObject(validationObject);

            if (validationErrors.size() == 0) {
                record.setRecordStatus(RecordEntity.RecordStatus.VALID);
            } else {
                record.setValidationErrors(validationErrors);
                record.setRecordStatus(RecordEntity.RecordStatus.INVALID);
            }

            record.setLastChangedDate(Instant.now());
            recordDao.merge(record);
        }
    }

    /**
     * Validate a set of recordEntities which have been created with samples
     * The validation is against the associated validation object
     * The JSON stored in the record by the
     *
     * @param recordEntities The recordEntities to validate
     */
    @Transactional
    public void validate(Collection<RecordEntity> recordEntities) {
        // Deserialize the list of samples from the JSON
        // field in the record and pass store in a map
        Map<RecordEntity, AbstractValidationObject> recordEntityAbstractValidationObjectMap = recordEntities.stream()
                .filter(r -> r.getJson() != null && !r.getJson().isEmpty())
                .collect(Collectors.toMap(
                        r -> r,
                        r -> {

                            Payload payload;

                            try {
                                payload = mapper.readValue(r.getJson(), Payload.class);
                            } catch (IOException e) {
                                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                                return null;
                            }

                            // Create a validation object using the factory
                            return validationObjectFactory.create(payload);

                        }
                ));

        // Validate the AbstractValidationObject measurement record and store the result of the validation
        // as the validation result serialized to json
        recordEntityAbstractValidationObjectMap.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(m -> {

                    Set<ValidationError> validationErrors = validator.validateValidationObject(m.getValue());

                    if (validationErrors.size() == 0) {
                        m.getKey().setRecordStatus(RecordEntity.RecordStatus.VALID);
                    } else {
                        m.getKey().setValidationErrors(validationErrors);
                        m.getKey().setRecordStatus(RecordEntity.RecordStatus.INVALID);
                    }
                    recordDao.merge(m.getKey());
                });
    }

    /**
     * Submit a set of valid recordEntities - this writes the data from the stored JSON into the
     * relation database structures - it creates an instance of a class inheriting AbstractPayloadEntity
     * and associates it with the record.
     * <p>
     * It will ignore all recordEntities that are invalid
     *
     * @param recordEntities
     */
    @Transactional
    public void submit(Collection<RecordEntity> recordEntities) {

        // The timestamp
        Instant timestamp = Instant.now();

        // This can potentially submit multiple datasets. Only if all of the records
        // on a dataset are in the valid status will the dataset be submitted
        Map<DatasetEntity, List<RecordEntity>> datasets = recordEntities
                .stream().collect(Collectors.groupingBy(RecordEntity::getDataset));

        Map<DatasetEntity, Map<RecordEntity.RecordStatus, Long>> datasetStatusMap = datasets.entrySet()
                .stream()
                .collect(Collectors.toMap(me -> me.getKey(),
                        me -> me.getValue()
                                .stream()
                                .collect(Collectors.groupingBy(RecordEntity::getRecordStatus, Collectors.counting()))
                ));

        for (DatasetEntity datasetEntity : datasetStatusMap.keySet()) {
            // Check all the records are valid
            if (datasetStatusMap.get(datasetEntity).get(RecordEntity.RecordStatus.VALID) ==
                    datasets.get(datasetEntity).size()) {

                // Process the records of each dataset
                datasets.entrySet()
                        .stream()
                        .filter(m -> m.getKey().equals(datasetEntity))
                        .map(Map.Entry::getValue)
                        .forEach(l -> l.stream().forEach(r -> {
                            try {
                                Payload payload = mapper.readValue(r.getJson(), Payload.class);
                                AbstractPayloadEntityFactory factory = AbstractPayloadEntityFactory.factoryFor(payload.getClass());
                                AbstractPayloadEntity submission = factory.create(payload);
                                r.setAbstractPayloadEntity(submission);
                                r.getAbstractPayloadEntity().setRecordEntity(r);
                                r.setLastChangedDate(timestamp);
                                r.setValidationErrors(null);
                                payloadEntityDao.persist(submission);
                                recordDao.merge(r);
                            } catch (IOException e) {
                                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                            }
                        }));

                // Process the dataset status
                datasets.entrySet()
                        .stream()
                        .filter(m -> m.getKey().equals(datasetEntity))
                        .map(Map.Entry::getKey)
                        .forEach(d -> {
                            d.setRecordChangedDate(timestamp);
                            d.setLastChangedDate(timestamp);
                            d.setStatus(DatasetEntity.Status.SUBMITTED);
                            datasetDao.merge(d);
                        });

            }
        }
    }

    /**
     * Evaluate the set of substitutions used for a given collection of records
     * @param recordEntities
     * @return
     */
    @Transactional
    public List<RecordEntity> evaluateSubstitutes(Collection<RecordEntity> recordEntities) {
        return recordEntities.stream()
                .filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID)
                .map(r -> {
                    try {
                        Payload payload = mapper.readValue(r.getJson(), Payload.class);
                        AbstractPayloadEntity submission = AbstractPayloadEntityFactory.factoryFor(payload.getClass()).create(payload);
                        r.setAbstractPayloadEntity(submission);
                        return r;
                    } catch (IOException e) {
                        LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                        return r;
                    }
                })
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
     * Any record on a dataset with a status of SUBMITTED remains unaffected
     * by this call
     *
     * @return A record entity
     */
    private <D extends Payload> RecordEntity createOrResetRecord(DatasetEntity dataset, String identifier, D payload) {
        Instant timestamp = Instant.now();

        RecordEntity recordEntity = getOrCreateRecord(dataset, identifier);

        if (dataset.getStatus() == DatasetEntity.Status.SUBMITTED) {
            return recordEntity;
        }

        recordEntity = updateRecord(recordEntity, payload);

        dataset.setRecordChangedDate(timestamp);
        datasetDao.merge(dataset);

        return recordEntity;
    }

    private <D extends Payload> RecordEntity updateRecord(RecordEntity recordEntity, D payload) {
        RecordEntity.RecordStatus newRecordStatus = recordEntity.getRecordStatus();

        // If we have a data transfer object, associated
        if (payload != null) {
            try {
                String json = mapper.writeValueAsString(payload);
                recordEntity.setJson(json);
                recordEntity.setRecordStatus(RecordEntity.RecordStatus.PARSED);
                recordEntity.setPayloadType(payload.getPayloadType());
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot serialize to Json: " + payload.toString());
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
     * Returns a submitted record entity for a dataset and identifier
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public RecordEntity retrieve(DatasetEntity dataset, String id) {
        return recordDao.get(dataset, id);
    }


    @Transactional(readOnly = true)
    public List<Triple<String, String, String>> retrieveValidationErrors(DatasetEntity dataset) {
        return recordDao.getValidationErrors(dataset);
    }

    /**
     * Returns the set of submitted record entities in a dataset
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public List<RecordEntity> retrieve(DatasetEntity dataset) {
        return recordDao.list(dataset, RecordDao.FetchType.FETCH_BASE);
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
            recordEntity = createRecord(dataset, identifier);
        }
        return recordEntity;
    }

    private RecordEntity createRecord(DatasetEntity dataset, String identifier) {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setDataset(dataset);
        recordEntity.setIdentifier(identifier);
        recordEntity.setRecordStatus(RecordEntity.RecordStatus.CREATED);

        Instant timestamp = Instant.now();
        recordEntity.setCreateDate(timestamp);
        recordEntity.setLastChangedDate(timestamp);
        return recordEntity;
    }

}
