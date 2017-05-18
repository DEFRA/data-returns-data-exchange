package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityAdaptor;

import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author Graham Willis
 */
public class DatasetAdaptor implements EntityAdaptor<Dataset, DatasetEntity> {

    private static final DatasetAdaptor datasetAdaptor = new DatasetAdaptor();

    private DatasetAdaptor() {
    }

    public static DatasetAdaptor getInstance() {
        return datasetAdaptor;
    }

    @Override
    public DatasetEntity convert(Dataset dataset) {
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setIdentifier(dataset.getId());
        if (dataset.getProperties() != null) {
            datasetEntity.setOriginatorEmail(dataset.getProperties().getOriginatorEmail());
        }
        return datasetEntity;
    }

    @Override
    public DatasetEntity merge(DatasetEntity datasetEntity, Dataset dataset) {
        if (datasetEntity == null) {
            return convert(dataset);
        }
        datasetEntity.setIdentifier(dataset.getId());
        if (dataset.getProperties() != null) {
            datasetEntity.setOriginatorEmail(dataset.getProperties().getOriginatorEmail());
        }
        return datasetEntity;
    }

    @Override
    public Dataset convert(DatasetEntity datasetEntity) {
        if (datasetEntity == null) {
            return null;
        }

        Dataset dataset = new Dataset();
        dataset.setId(datasetEntity.getIdentifier());
        dataset.setCreated(Date.from(datasetEntity.getCreateDate()));

        // TODO: Graham, this feels a little hacky but will do for now - would be nice to have a properties object associated
        // with the dataset entity so we could test for its existence instead of checking the originator email
        if (datasetEntity.getOriginatorEmail() != null) {
            DatasetProperties datasetProperties = new DatasetProperties();
            datasetProperties.setOriginatorEmail(datasetEntity.getOriginatorEmail());
            dataset.setProperties(datasetProperties);
        }

        return dataset;
    }
}