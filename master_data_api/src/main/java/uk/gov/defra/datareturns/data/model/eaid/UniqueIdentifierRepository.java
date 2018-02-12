package uk.gov.defra.datareturns.data.model.eaid;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;
import uk.gov.defra.datareturns.data.model.site.Site;

import java.util.List;


/**
 * Spring REST repository for {@link UniqueIdentifier} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface UniqueIdentifierRepository extends MasterDataRepository<UniqueIdentifier> {
    /**
     * Retrieve the unique identifiers associated with the given site
     *
     * @param site the site used to look up the unique identifiers
     * @return a {@link List} of {@link UniqueIdentifier} objects
     */
    @SuppressWarnings("unused")
    List<UniqueIdentifier> findUniqueIdentifiersBySite(@Param("site") Site site);

    /**
     * Retrieve the unique identifiers associated with the given site
     *
     * @param nomenclature the site name used to look up the unique identifiers
     * @return a {@link List} of {@link UniqueIdentifier} objects
     */
    @SuppressWarnings("unused")
    List<UniqueIdentifier> findUniqueIdentifiersBySiteNomenclature(@Param("nomenclature") String nomenclature);
}
