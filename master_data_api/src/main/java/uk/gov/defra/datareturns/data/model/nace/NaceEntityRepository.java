package uk.gov.defra.datareturns.data.model.nace;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Base REST repository for all NACE related entities.
 *
 * @author Sam Gardner-Dell
 */
@NoRepositoryBean
public interface NaceEntityRepository<E extends AbstractNaceEntity> extends BaseRepository<E, Long> {
    /**
     * Retrieve a NACE entry by its code
     *
     * @param nomenclature the code to lookup the NACE entry
     * @return the NACE entry for the given code or null if not founnd
     */
    @SuppressWarnings("unused")
    E getByNomenclature(@Param("nomenclature") final String nomenclature);
}
