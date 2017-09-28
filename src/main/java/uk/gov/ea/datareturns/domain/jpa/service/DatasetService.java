package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetCollectionRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetRepository;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Graham Willis
 */
@Component
public class DatasetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetService.class);
    private final UniqueIdentifierRepository uniqueIdentifierRepository;
    private final DatasetCollectionRepository datasetCollectionRepository;
    private final DatasetRepository datasetRepository;
    private final PayloadEntityDao payloadDao;

    @Inject
    public DatasetService(UniqueIdentifierRepository uniqueIdentifierRepository,
            DatasetCollectionRepository datasetCollectionRepository,
            DatasetRepository datasetRepository, PayloadEntityDao payloadDao) {
        this.uniqueIdentifierRepository = uniqueIdentifierRepository;
        this.datasetCollectionRepository = datasetCollectionRepository;
        this.datasetRepository = datasetRepository;
        this.payloadDao = payloadDao;
        LOGGER.info("Initializing dataset service");
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
    public void createDataset(String uniqueIdentifierId, DatasetEntity newDatasetEntity) {
        Instant timestamp = Instant.now();

        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName(uniqueIdentifierId);
        if (uniqueIdentifier == null) {
            return;
        }
        DatasetCollection collection = Optional.ofNullable(datasetCollectionRepository.getByUniqueIdentifier(uniqueIdentifier))
                .orElseGet(() -> createCollection(uniqueIdentifier));

        newDatasetEntity.setCreateDate(timestamp);
        newDatasetEntity.setLastChangedDate(timestamp);
        newDatasetEntity.setRecordChangedDate(timestamp);
        newDatasetEntity.setStatus(DatasetEntity.Status.UNSUBMITTED);
        newDatasetEntity.setParentCollection(collection);

        if (newDatasetEntity.getIdentifier() == null) {
            newDatasetEntity.setIdentifier(UUID.randomUUID().toString());
        }

        datasetRepository.saveAndFlush(newDatasetEntity);
    }

    @Transactional
    public void updateDataset(DatasetEntity datasetEntity) {
        Instant timestamp = Instant.now();
        datasetEntity.setLastChangedDate(Instant.now());
        datasetRepository.save(datasetEntity);

        DatasetCollection collection = datasetEntity.getParentCollection();
        collection.setLastChangedDate(timestamp);
        datasetCollectionRepository.saveAndFlush(collection);
    }

    @Transactional
    public DatasetCollection getDatasets(UniqueIdentifier uniqueIdentifier) {
        return Optional.ofNullable(datasetCollectionRepository.getByUniqueIdentifier(uniqueIdentifier))
                .orElseGet(() -> this.createCollection(uniqueIdentifier));
    }

    @Transactional
    public void removeDataset(String uniqueIdentifierId, String datasetIdentifier) {
        Instant timestamp = Instant.now();
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName(uniqueIdentifierId);
        DatasetCollection collection = datasetCollectionRepository.getByUniqueIdentifier(uniqueIdentifier);
        DatasetEntity dataset = datasetRepository.getByParentCollectionAndIdentifier(collection, datasetIdentifier);
        if (dataset != null) {
            payloadDao.removeAll(dataset);
            collection.getDatasets().remove(dataset);
            collection.setLastChangedDate(timestamp);

            datasetRepository.delete(dataset);
            datasetRepository.flush();

            datasetCollectionRepository.saveAndFlush(collection);
        }
    }

    @Transactional(readOnly = true)
    public DatasetEntity getDataset(String uniqueIdentifierId, String datasetIdentifier) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName(uniqueIdentifierId);
        DatasetCollection collection = datasetCollectionRepository.getByUniqueIdentifier(uniqueIdentifier);
        return datasetRepository.getByParentCollectionAndIdentifier(collection, datasetIdentifier);
    }

    private DatasetCollection createCollection(UniqueIdentifier uniqueIdentifier) {
        Instant timestamp = Instant.now();
        DatasetCollection collection = new DatasetCollection();
        collection.setUniqueIdentifier(uniqueIdentifier);
        collection.setDatasets(new ArrayList<>());
        collection.setCreateDate(timestamp);
        collection.setLastChangedDate(timestamp);
        datasetCollectionRepository.saveAndFlush(collection);
        return collection;
    }
}
