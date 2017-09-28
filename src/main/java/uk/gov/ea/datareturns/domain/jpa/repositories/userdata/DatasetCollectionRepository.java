package uk.gov.ea.datareturns.domain.jpa.repositories.userdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

/**
 * Repository for dataset collections
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface DatasetCollectionRepository extends BaseRepository<DatasetCollection, Long> {
    DatasetCollection getByUniqueIdentifier(UniqueIdentifier owner);
}