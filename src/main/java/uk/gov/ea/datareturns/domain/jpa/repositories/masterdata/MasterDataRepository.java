package uk.gov.ea.datareturns.domain.jpa.repositories.masterdata;

import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

/**
 * Common repository definition for master data entities
 *
 * @param <E> the type of the entity
 * @author Sam Gardner-Dell
 */
@NoRepositoryBean
public interface MasterDataRepository<E extends MasterDataEntity> extends BaseRepository<E, Long> {
    /**
     * Retrieve a master data entity by its name
     *
     * @param name the name of entity to retrieve
     * @return The entity E or null
     */
    E getByName(String name);
}