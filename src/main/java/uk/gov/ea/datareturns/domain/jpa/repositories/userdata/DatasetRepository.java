package uk.gov.ea.datareturns.domain.jpa.repositories.userdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.util.List;

/**
 * Repository for datasets
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface DatasetRepository extends BaseRepository<DatasetEntity, Long> {

    /**
     * Get a dataset for the given user and dataset identifier
     *
     * @param user the user to which the dataset belongs
     * @param identifier the dataset identifier
     * @return the target {@link DatasetEntity} or null if not found
     */
    DatasetEntity getByUserAndIdentifier(User user, String identifier);

    /**
     * Remmove a dataset for the given user and dataset identifier
     *
     * @param user the user to which the dataset belongs
     * @param identifier the dataset identifier
     * @return the target {@link DatasetEntity} or null if not found
     */
    void removeByUserAndIdentifier(User user, String identifier);

    /**
     * Find all datasets belonging to the given user
     *
     * @param user the user whose datasets to find
     * @return a {@link List} of {@link DatasetEntity}
     */
    List<DatasetEntity> findAllByUser(User user);
}