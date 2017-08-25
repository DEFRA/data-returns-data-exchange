package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Graham Willis
 */
@Component
public class DatasetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetService.class);
    private final DatasetDao datasetDao;
    private final PayloadEntityDao payloadDao;
    private final UniqueIdentifierDao uniqueIdentifierDao;

    @Inject
    public DatasetService(UniqueIdentifierDao uniqueIdentifierDao, DatasetDao datasetDao, PayloadEntityDao payloadDao) {
        this.datasetDao = datasetDao;
        this.payloadDao = payloadDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
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
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(uniqueIdentifierId);

        newDatasetEntity.setCreateDate(timestamp);
        newDatasetEntity.setLastChangedDate(timestamp);
        newDatasetEntity.setRecordChangedDate(timestamp);
        newDatasetEntity.setStatus(DatasetEntity.Status.UNSUBMITTED);
        newDatasetEntity.setUniqueIdentifier(uniqueIdentifier);

        if (newDatasetEntity.getIdentifier() == null) {
            newDatasetEntity.setIdentifier(UUID.randomUUID().toString());
        }

        uniqueIdentifier.setDatasetChangedDate(timestamp);
        uniqueIdentifierDao.merge(uniqueIdentifier);
        datasetDao.persist(newDatasetEntity);
    }

    @Transactional
    public void updateDataset(DatasetEntity datasetEntity) {
        Instant timestamp = Instant.now();
        datasetEntity.setLastChangedDate(Instant.now());
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(
                datasetEntity.getUniqueIdentifier().getName());
        uniqueIdentifier.setDatasetChangedDate(timestamp);
        uniqueIdentifierDao.merge(uniqueIdentifier);
        datasetDao.merge(datasetEntity);
    }

    @Transactional(readOnly = true)
    public List<DatasetEntity> getDatasets(String uniqueIdentifierId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(uniqueIdentifierId);
        return datasetDao.list(uniqueIdentifier);
    }

    @Transactional
    public void removeDataset(String identifier, String uniqueIdentifierId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(uniqueIdentifierId);
        DatasetEntity dataset = datasetDao.get(uniqueIdentifier, identifier);
        if (dataset != null) {
            payloadDao.removeAll(dataset);
            datasetDao.remove(dataset.getId());
            Instant timestamp = Instant.now();
            uniqueIdentifier.setDatasetChangedDate(timestamp);
            uniqueIdentifierDao.merge(uniqueIdentifier);
        }
    }

    @Transactional(readOnly = true)
    public DatasetEntity getDataset(String datasetId, String uniqueIdentifierId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(uniqueIdentifierId);
        return datasetDao.get(uniqueIdentifier, datasetId);
    }
}
