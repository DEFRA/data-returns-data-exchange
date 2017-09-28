package uk.gov.ea.datareturns.domain.jpa.repositories.userdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.util.List;

/**
 * Repository for datasets
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface DatasetRepository extends BaseRepository<DatasetEntity, Long> {
    List<DatasetEntity> findAllByParentCollection(DatasetCollection collection);
    DatasetEntity getByParentCollectionAndIdentifier(DatasetCollection collection, String identifier);
}