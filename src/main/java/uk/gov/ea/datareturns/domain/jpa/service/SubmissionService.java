package uk.gov.ea.datareturns.domain.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.exceptions.UnsubmittableDatasetException;
import uk.gov.ea.datareturns.domain.exceptions.UnsubmittableRecordsException;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.EntitySubstitution;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.TranslationResult;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.projections.userdata.ValidationErrorInstance;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.RecordRepository;
import uk.gov.ea.datareturns.domain.validation.common.validator.AbstractValidationObject;
import uk.gov.ea.datareturns.domain.validation.common.validator.ValidationObjectFactory;
import uk.gov.ea.datareturns.domain.validation.common.validator.Validator;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    private final RecordRepository recordRepository;
    private final PayloadEntityDao payloadEntityDao;
    private final Validator<AbstractValidationObject> validator;
    private final static ObjectMapper mapper = new ObjectMapper();
    private final DatasetRepository datasetRepository;

    public SubmissionService(ValidationObjectFactory validationObjectFactory,
            DatasetRepository datasetRepository,
            RecordRepository recordRepository,
            PayloadEntityDao payloadEntityDao,
            Validator<AbstractValidationObject> validator) {

        this.validationObjectFactory = validationObjectFactory;
        this.datasetRepository = datasetRepository;
        this.recordRepository = recordRepository;
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
     * Returns a submitted record entity for a dataset and identifier
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public RecordEntity retrieve(DatasetEntity dataset, String id) {
        return recordRepository.getByDatasetAndIdentifier(dataset, id);
    }

    @Transactional(readOnly = true)
    public List<ValidationErrorInstance> retrieveValidationErrors(DatasetEntity dataset) {
        // TODO: This is only necessary due to a bug with spring data projections and native queries, see https://jira.spring.io/browse/DATAJPA-980
        // Once this has been fixed, the recordRepository.getValidationErrorsForDataset could return a List<RecordRepository.ValidationErrorInstance> itself
        // See RecordRepository
        List<Object[]> results = recordRepository.getValidationErrorsForDataset(dataset);
        List<ValidationErrorInstance> errors = new ArrayList<>(results.size());
        for (Object[] entry : results) {
            errors.add(new ValidationErrorInstance() {
                @Override public String getRecordIdentifier() {
                    return Objects.toString(entry[0], null);
                }

                @Override public String getPayloadType() {
                    return Objects.toString(entry[1], null);
                }

                @Override public String getConstraintIdentifier() {
                    return Objects.toString(entry[2], null);
                }
            });
        }
        return errors;
    }

    /**
     * Returns the set of submitted record entities in a dataset
     *
     * @param dataset
     */
    @Transactional(readOnly = true)
    public List<AbstractPayloadEntity> getPayloadList(DatasetEntity dataset) {
        return payloadEntityDao.list(dataset);
    }

    /**
     * Get a list of existing records for a given dataset
     *
     * @param dataset A dataset
     * @return A list of records
     */
    @Transactional(readOnly = true)
    public List<RecordEntity> getRecords(DatasetEntity dataset) {
        return recordRepository.findAllByDataset(dataset);
    }

    /**
     * Retrieve a single record null if not found
     * @param dataset A dataset
     * @param identifier A record identifier
     * @return the record
     */
    @Transactional(readOnly = true)
    public RecordEntity getRecord(DatasetEntity dataset, String identifier) {
        return recordRepository.getByDatasetAndIdentifier(dataset, identifier);
    }

    /**
     * Remove a record using its identifier
     *
     * @param dataset    The dataset
     * @param identifier the record identifier
     */
    @Transactional
    public void removeRecord(DatasetEntity dataset, String identifier) {
        RecordEntity record = getRecord(dataset, identifier);
        if (record != null) {
            payloadEntityDao.remove(record.getId(), AbstractPayloadEntity.class);
            recordRepository.delete(record);
            dataset.setRecordChangedDate(Instant.now());
            datasetRepository.saveAndFlush(dataset);
        }
    }

    /**
     * Test if a record exists in a given dataset by its identifier
     *
     * @param dataset    The dataset
     * @param identifier The identifier
     * @return
     */
    public boolean recordExists(DatasetEntity dataset, String identifier) {
        return getRecord(dataset, identifier) != null;
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
        Map<String, RecordEntity> existingRecords = getRecords(dataset)
                .stream()
                .filter(r -> payloadMap.keySet().contains(r.getIdentifier()))
                .collect(Collectors.toMap(RecordEntity::getIdentifier, r -> r));

        Map<String, RecordEntity> updatedRecords = new LinkedHashMap<>();
        payloadMap.forEach((identifier, payload) -> {
            RecordEntity recordEntity = Optional.ofNullable(existingRecords.get(identifier)).orElse(new RecordEntity(dataset, identifier));
            updatedRecords.put(identifier, updateRecord(recordEntity, payload));
        });
        // Update the dataset's record collection timestamp
        dataset.setRecordChangedDate(Instant.now());
        recordRepository.flush();
        datasetRepository.saveAndFlush(dataset);

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
        Instant timestamp = Instant.now();
        RecordEntity recordEntity = Optional.ofNullable(recordRepository.getByDatasetAndIdentifier(dataset, identifier))
                .orElse(new RecordEntity(dataset, identifier));

        if (dataset.getStatus() == DatasetEntity.Status.SUBMITTED) {
            return recordEntity;
        }

        recordEntity = updateRecord(recordEntity, payload);

        dataset.setRecordChangedDate(timestamp);
        recordRepository.flush();
        datasetRepository.saveAndFlush(dataset);

        return recordEntity;
    }

    /**
     * Submit the specified dataset.
     *
     * If the dataset is not valid then it will not be submitted and this method shall throw an exception
     * This writes the data from the stored JSON into the relation database structures - it creates an instance of a class inheriting
     * AbstractPayloadEntity and associates it with the record.
     *
     * @param datasetEntity the dataset to be submitted
     */
    @Transactional
    public void submit(DatasetEntity datasetEntity) throws ProcessingException {
        // The timestamp
        Instant timestamp = Instant.now();

        // Validate the dataset is submittable first
        EmailValidator emailValidator = new EmailValidator();
        if (StringUtils.isBlank(datasetEntity.getOriginatorEmail()) || !emailValidator.isValid(datasetEntity.getOriginatorEmail(), null)) {
            throw new UnsubmittableDatasetException("The originator email specified is not valid.");
        }

        // Now check that all records belonging to the dataset are submittable
        Collection<RecordEntity> recordEntities = getRecords(datasetEntity);
        boolean hasInvalid = recordEntities.stream().anyMatch(r -> r.getRecordStatus() != RecordEntity.RecordStatus.VALID);

        if (hasInvalid) {
            throw new UnsubmittableRecordsException("The dataset contains records with validation errors.");
        }

        // Create payload entry for all records
        for (RecordEntity r : recordEntities) {
            try {
                Payload payload = mapper.readValue(r.getJson(), Payload.class);
                AbstractPayloadEntityFactory<AbstractPayloadEntity, Payload> factory = AbstractPayloadEntityFactory
                        .genericFactory(payload.getClass());
                TranslationResult<AbstractPayloadEntity> result = factory.create(payload);
                AbstractPayloadEntity submission = result.getEntity();
                submission.setDataset(datasetEntity);
                submission.setRecordEntity(r);
                payloadEntityDao.persist(submission);
            } catch (IOException e) {
                LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
            }
        }
        // Update the dataset
        datasetEntity.setRecordChangedDate(timestamp);
        datasetEntity.setLastChangedDate(timestamp);
        datasetEntity.setStatus(DatasetEntity.Status.SUBMITTED);
        datasetRepository.saveAndFlush(datasetEntity);
    }

    /**
     * Evaluate the set of substitutions used for a given collection of records
     * @param recordEntities
     * @return
     */
    @Transactional
    public Map<RecordEntity, Set<EntitySubstitution>> evaluateSubstitutes(Collection<RecordEntity> recordEntities) {

        Map<RecordEntity, Set<EntitySubstitution>> substitutions = new LinkedHashMap<>();
        for (RecordEntity record : recordEntities) {
            if (record.getRecordStatus() == RecordEntity.RecordStatus.VALID) {
                try {
                    Payload payload = mapper.readValue(record.getJson(), Payload.class);
                    AbstractPayloadEntityFactory<AbstractPayloadEntity, Payload> factory = AbstractPayloadEntityFactory
                            .genericFactory(payload.getClass());
                    TranslationResult<AbstractPayloadEntity> result = factory.create(payload);
                    if (result.getSubstitutions() != null) {
                        substitutions.put(record, result.getSubstitutions());
                    }
                } catch (IOException e) {
                    LOGGER.error("Error de-serializing stored JSON: " + e.getMessage());
                }
            }
        }
        return substitutions;
    }

    private <D extends Payload> RecordEntity updateRecord(RecordEntity recordEntity, D payload) {
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

        // Validate the payload associated with the record
        validate(recordEntity, payload);

        recordEntity.setLastChangedDate(Instant.now());
        recordRepository.save(recordEntity);
        return recordEntity;
    }

    /**
     * Validate the given payload associated with the specified record
     * @param record the record the payload belongs to
     * @param payload the payload to be validated.
     */
    private void validate(RecordEntity record, Payload payload) {
        Set<ValidationError> validationErrors = null;
        if (payload != null) {
            AbstractValidationObject validationObject = validationObjectFactory.create(payload);
            validationErrors = validator.validateValidationObject(validationObject);
        }

        RecordEntity.RecordStatus status = RecordEntity.RecordStatus.INVALID;
        if (payload != null && validationErrors.isEmpty()) {
            // Only set to valid if the payload is not null and there are no errors having validated it
            status = RecordEntity.RecordStatus.VALID;
        }
        record.setValidationErrors(validationErrors);
        record.setRecordStatus(status);
    }
}